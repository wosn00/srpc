package com.hex.srpc.core.connection;

import java.util.List;

/**
 * @author guohs
 * @date 2021/7/15
 *
 * 每个节点的连接池，负责连接的生命周期
 */
public interface IConnectionPool {

    List<IConnection> getAllConnections();

    IConnection getConnection();

    void addConnection(IConnection connection);

    void releaseConnection(Long id);

    int currentSize();

    void close();

}
