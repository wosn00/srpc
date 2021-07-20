package com.hex.netty.connection;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: hs
 */
public interface ServerManager {

    void addServer(InetSocketAddress server);

    void addServers(List<InetSocketAddress> servers);

    void removeServer(InetSocketAddress server);

    InetSocketAddress[] getAllRemoteServers();

    int getServersNum();

    InetSocketAddress selectServer(List<InetSocketAddress> servers);

    Connection chooseConnection(List<InetSocketAddress> servers);

    Connection chooseConnection();

    void closeManager();

}
