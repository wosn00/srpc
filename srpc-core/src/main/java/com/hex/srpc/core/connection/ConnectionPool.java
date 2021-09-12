package com.hex.srpc.core.connection;

import com.hex.common.exception.RpcException;
import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.node.NodeManager;
import com.hex.srpc.core.rpc.client.SRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * 连接池,每个rpc server(node)一个连接池
 */
public class ConnectionPool implements IConnectionPool {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionPool.class);

    private int maxSize;
    private HostAndPort remoteAddress;
    private SRpcClient client;
    private final List<IConnection> connections = new CopyOnWriteArrayList<>();
    private final AtomicInteger counter = new AtomicInteger(0);
    private final AtomicBoolean isClosed = new AtomicBoolean(false);

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();


    public ConnectionPool(int maxSize, HostAndPort remoteAddress, SRpcClient client) {
        this.maxSize = maxSize;
        this.remoteAddress = remoteAddress;
        this.client = client;
    }

    @Override
    public List<IConnection> getAllConnections() {
        return connections;
    }

    @Override
    public IConnection getConnection() {
        if (isClosed.get()) {
            return null;
        }
        connectionInit();
        readLock.lock();
        try {
            if (!connections.isEmpty()) {
                IConnection connection = connections.get(incrementAndGetModulo(size()));
                if (!connection.isAvailable()) {
                    releaseConnection(connection.getId());
                    connection = getConnection();
                }
                return connection;
            }
        } finally {
            readLock.unlock();
        }
        throw new RpcException("no connection available, node: " + remoteAddress);
    }

    @Override
    public void addConnection(IConnection connection) {
        connections.add(connection);
    }

    @Override
    public void releaseConnection(Long id) {
        writeLock.lock();
        try {
            for (IConnection connection : connections) {
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
                Iterator<IConnection> iterator = connections.iterator();
                while (iterator.hasNext()) {
                    IConnection connection = iterator.next();
                    if (connection.isAvailable()) {
                        connection.close();
                    }
                    connections.remove(connection);
                }
                connections.clear();
                logger.info("connectionPool {}  closed, release all connections", remoteAddress);
            } catch (Exception e) {
                logger.error("connectionPool {}  closed failed!", remoteAddress, e);
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
                    IConnection connection = this.client.connect(remoteAddress.getHost(), remoteAddress.getPort());
                    if (connection.isAvailable()) {
                        addConnection(connection);
                    } else {
                        retryTimes++;
                    }
                } catch (Exception e) {
                    retryTimes++;
                    NodeManager.serverError(new HostAndPort(remoteAddress.getHost(), remoteAddress.getPort()));
                    logger.error("server {} connectionPool init failed", remoteAddress, e);
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
