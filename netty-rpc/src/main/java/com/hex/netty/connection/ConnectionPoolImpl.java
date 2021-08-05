package com.hex.netty.connection;

import com.hex.netty.rpc.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author guohs
 * @date 2021/7/15
 * 连接池,每个server一个连接池
 */
public class ConnectionPoolImpl implements ConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPoolImpl.class);

    private int maxSize;
    private InetSocketAddress remoteAddress;
    private Client client;
    private final List<Connection> connections = new CopyOnWriteArrayList<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();


    public ConnectionPoolImpl(int maxSize, InetSocketAddress remoteAddress, Client client) {
        this.maxSize = maxSize;
        this.remoteAddress = remoteAddress;
        this.client = client;
    }

    @Override
    public List<Connection> getAllConnections() {
        return connections;
    }

    @Override
    public Connection getConnection() {
        if (isClosed.get()) {
            return null;
        }
        connectionInit();
        readLock.lock();
        try {
            if (!connections.isEmpty()) {
                Connection connection = connections.get(incrementAndGetModulo(size()));
                if (!connection.isAvailable()) {
                    releaseConnection(connection.getId());
                    connection = getConnection();
                }
                return connection;
            }
        } finally {
            readLock.unlock();
        }
        return null;
    }

    @Override
    public void addConnection(Connection connection) {
        connections.add(connection);
    }

    @Override
    public void releaseConnection(String id) {
        writeLock.lock();
        try {
            for (Connection connection : connections) {
                if (connection.getId().equals(id)) {
                    connections.remove(connection);
                    if (connection.isAvailable()) {
                        connection.close();
                    }
                }
            }
        } catch (Exception e) {
            logger.error("releaseConnection failed", e);
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public int size() {
        return connections.size();
    }


    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            writeLock.lock();
            try {
                // 关闭所有连接
                Iterator<Connection> iterator = connections.iterator();
                while (iterator.hasNext()) {
                    Connection connection = iterator.next();
                    if (connection.isAvailable()) {
                        connection.close();
                    }
                    iterator.remove();
                }
                connections.clear();
                logger.info("connectionPool [{}]  closed, release all connections", remoteAddress);
            } catch (Exception e) {
                logger.error("connectionPool [{}]  closed failed!", remoteAddress, e);
            } finally {
                writeLock.unlock();
            }
        }
    }

    private void connectionInit() {
        int retryTimes = 0;
        writeLock.lock();
        try {
            while (size() < maxSize && retryTimes < 3) {
                try {
                    Connection connection = this.client.connect(remoteAddress.getHostString(), remoteAddress.getPort());
                    if (connection.isAvailable()) {
                        addConnection(connection);
                    } else {
                        retryTimes++;
                    }
                } catch (Exception e) {
                    retryTimes++;
                    logger.error("server [{}] connectionPool init failed", remoteAddress, e);
                }
            }
        } finally {
            writeLock.unlock();
        }

    }

    private int incrementAndGetModulo(int modulo) {
        for (; ; ) {
            int current = counter.get();
            int next = (current + 1) % modulo;
            if (counter.compareAndSet(current, next)) {
                return next;
            }
        }
    }

}
