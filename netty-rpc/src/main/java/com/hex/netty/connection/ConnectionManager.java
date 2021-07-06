package com.hex.netty.connection;

/**
 * @author: hs
 */
public interface ConnectionManager {

    Connection getConnById(String id);

    Connection[] getAllConn();

    void addConn(Connection connection);

    boolean removeConn(String id);

    boolean close();

    Connection getConn();

    int size();

}
