package com.hex.netty.connection;

import java.util.List;

/**
 * @author guohs
 * @date 2021/7/15
 */
public interface ConnectionPool {

    List<Connection> getAllConnections();

    Connection getConnection();

    void releaseConnection(String id);

    int size();

    void close();

}
