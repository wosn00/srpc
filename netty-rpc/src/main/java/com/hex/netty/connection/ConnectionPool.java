package com.hex.netty.connection;

/**
 * @author guohs
 * @date 2021/7/15
 */
public interface ConnectionPool {

    Connection getConnection();

    void release(Connection connection);

    int totalSize();

    void closeConnection(String connId);

    void closePool();

}
