package com.hex.netty.connection;

import com.hex.netty.exception.RpcException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: hs
 */
public class DefaultConnectionManager implements ConnectionManager {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Queue<Connection> connectionQueue = new ConcurrentLinkedQueue<>();

    private AtomicBoolean isClosed = new AtomicBoolean(false);

    private final Object lock = new Object();

    @Override
    public Connection getConnById(String id) {
        for (Connection connection : connectionQueue) {
            if (connection.getId().equals(id)) {
                return connection;
            }
        }
        return null;
    }

    @Override
    public Connection[] getAllConn() {
        return connectionQueue.toArray(new Connection[0]);
    }

    @Override
    public void addConn(Connection connection) {
        if (StringUtils.isBlank(connection.getId())) {
            throw new RpcException("Failed to add connection, connection id can not be null!");
        }
        connectionQueue.add(connection);
    }

    @Override
    public boolean removeConn(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        boolean isRemoved = connectionQueue.remove(new NettyConnection(id));
        Connection connection = getConnById(id);
        if (isRemoved && connection.isAvailable()) {
            connection.close();
        }
        return isRemoved;
    }

    @Override
    public boolean close() {
        if (isClosed.compareAndSet(false, true)) {
            logger.info("connection manager closed, release all connections!");
            // 关闭所有连接
            synchronized (lock) {
                for (Connection connection : getAllConn()) {
                    removeConn(connection.getId());
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 轮询获取可用连接
     */
    @Override
    public Connection getConn() {
        Connection connection = null;
        synchronized (lock) {
            while (!connectionQueue.isEmpty()) {
                connection = connectionQueue.poll();
                if (connection.isAvailable()) {
                    connectionQueue.add(connection);
                    break;
                }
            }
        }
        return connection;
    }

    @Override
    public int size() {
        return connectionQueue.size();
    }

}
