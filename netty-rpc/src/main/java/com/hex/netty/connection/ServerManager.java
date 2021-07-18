package com.hex.netty.connection;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: hs
 */
public interface ServerManager {

    void addServer(InetSocketAddress server);

    void addServers(List<InetSocketAddress> servers);

    InetSocketAddress[] getAllServers();

    int getServersNum();

    InetSocketAddress selectServer(List<InetSocketAddress> servers);

    Connection getConnection(List<InetSocketAddress> servers);

    Connection getConnection();

}
