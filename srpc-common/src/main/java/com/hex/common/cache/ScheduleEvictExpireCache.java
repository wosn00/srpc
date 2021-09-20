package com.hex.common.cache;


import com.hex.common.thread.SRpcThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author guohs
 * @date 2021/9/15
 */
public class ScheduleEvictExpireCache<K, V> implements IExpireCache<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleEvictExpireCache.class);
    private static final long UNSET_INT = Long.MAX_VALUE;
    private long expireTimeMs;
    private long bulkTimeMs;
    private Node<K> headNode, lastNode;
    private Map<K, V> resultMap = new ConcurrentHashMap<>();
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Lock readLock = readWriteLock.readLock();
    private Lock writeLock = readWriteLock.writeLock();
    private ScheduledExecutorService schedule;
    private long maxSize;
    private static ThreadFactory threadFactory;

    public ScheduleEvictExpireCache(int expireTime, TimeUnit unit) {
        this(expireTime, unit, 10, UNSET_INT);
    }

    public ScheduleEvictExpireCache(int expireTime, TimeUnit unit, long maxSize) {
        this(expireTime, unit, 10, maxSize);
    }

    public ScheduleEvictExpireCache(int expireTime, TimeUnit unit, int bulkSize, long maxSize) {
        if (expireTime <= 0) {
            throw new RuntimeException("expireTime can't be low zero.");
        }

        this.expireTimeMs = unit.toMillis(expireTime);
        this.maxSize = maxSize;
        if (bulkSize <= 0) bulkSize = 10;
        this.bulkTimeMs = this.expireTimeMs / bulkSize;
        initScheduleExecutor();
        startSchedule();
    }


    private void initScheduleExecutor() {
        if (threadFactory == null) {
            synchronized (ScheduleEvictExpireCache.class) {
                if (threadFactory == null) {
                    threadFactory = new SRpcThreadFactory("srpc-cache-expire");
                }
            }
        }
        schedule = Executors.newScheduledThreadPool(1, threadFactory);
    }

    private void startSchedule() {
        schedule.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    evict();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }, expireTimeMs, bulkTimeMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 自动过期, 从头结点开始移除，与添加不冲突，可不需要进行加锁.
     */
    private void evict() {
        //可做去重作用.
        try {
            this.writeLock.lock();
            while (headNode != null
                    && (headNode.isExpire() || resultMap.size() > maxSize)) {
                for (K key : headNode.keys) {
                    resultMap.remove(key);
                }
                headNode = headNode.next;
            }
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public V put(K key, V value) {
        /*
         * 此部分无需加锁.
         */
        V result = resultMap.put(key, value);
        if (result != null) {
            //覆盖修改，过期时间还是按原来的计算
            return result;
        }

        /*
         * 变更node则才需要加锁.
         */
        try {
            this.writeLock.lock();
            if (headNode == null) {
                headNode = lastNode = new Node<>(key);
            } else {
                if (lastNode.isInCurInterval()) {
                    lastNode.addKey(key);
                } else {
                    lastNode = lastNode.next = new Node<>(key);
                }
            }
        } finally {
            this.writeLock.unlock();
        }
        return null;
    }

    @Override
    public V get(K key) {
        try {
            this.readLock.lock();
            return resultMap.get(key);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        try {
            this.readLock.lock();
            return resultMap.keySet();
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public void invalidate(K key) {
        try {
            this.writeLock.lock();
            resultMap.remove(key);
        } finally {
            this.writeLock.unlock();
        }
    }

    private long now() {
        return System.currentTimeMillis();
    }

    class Node<K> {
        private long timestamp;
        private Set<K> keys = new LinkedHashSet<>();
        private Node<K> next;

        private Node(K key) {
            this.timestamp = now();
            addKey(key);
        }

        private void addKey(K key) {
            keys.add(key);
        }

        private boolean isExpire() {
            return now() - timestamp > expireTimeMs;
        }

        private boolean isInCurInterval() {
            return now() - timestamp < bulkTimeMs;
        }
    }
}
