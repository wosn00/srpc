package com.hex.netty.connection;

import com.google.common.base.Throwables;
import com.hex.netty.rpc.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author guohs
 * @date 2021/7/15
 * 连接池,每个server一个连接池
 */
public class ServerConnectionPool implements ConnectionPool {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private int initSize;

    private int maxSize;

    private InetSocketAddress server;

    private Client client;

    private Map<String, Connection> used = new ConcurrentHashMap<>();

    private Queue<Connection> idle = new ConcurrentLinkedQueue<>();

    private AtomicInteger totalSize = new AtomicInteger(0);

    private Lock lock = new ReentrantLock();

    private Condition lendCond = lock.newCondition();

    public ServerConnectionPool(int initSize, int maxSize, InetSocketAddress server, Client client) {
        this.initSize = initSize;
        this.maxSize = maxSize;
        this.server = server;
        this.client = client;
    }

    public Connection getConnection() {
        Connection connection = idle.poll();
        if (connection != null) {
            return connection;
        }

        int size;
        while ((size = totalSize()) < maxSize && totalSize.compareAndSet(size, size + 1)) {
            //建立连接
            connection = this.client.connect(server.getHostName(), server.getPort());
            used.put(connection.getId(), connection);
            return connection;
        }
        try {
            lendCond.await(100, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.error(Throwables.getStackTraceAsString(e));
        }
        return getConnection();
    }

    @Override
    public void release(Connection connection) {

    }

    @Override
    public int totalSize() {
        return totalSize.get();
    }

    @Override
    public void closeConnection(String connId) {

    }

    @Override
    public void closePool() {

    }


}
