package com.hex.netty.connection;

import com.hex.netty.rpc.Client;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: hs
 */
public interface ServerManager {

    void addNode(InetSocketAddress nodeAddress);

    void addCluster(List<InetSocketAddress> cluster);

    void removeNode(InetSocketAddress nodeAddress);

    InetSocketAddress[] getAllRemoteNodes();

    int getNodesSize();

    ConnectionPool getConnectionPool(InetSocketAddress node);

    Map<String, AtomicInteger> getConnectionSize();

    Map<InetSocketAddress, ServerStatus> getServerStatusMap();

    Client getClient();

    /**
     * 根据集群节点选择高可用服务，负载均衡
     */
    InetSocketAddress chooseHANode(List<InetSocketAddress> cluster);

    /**
     * 根据集群节点选择出高可用连接，支持节点负载均衡，高可用性
     */
    Connection chooseHAConnection(List<InetSocketAddress> cluster);

    /**
     * 选择默认集群高可用连接，适用于Client只连接向一个集群的服务
     */
    Connection chooseHAConnection();

    /**
     * 指定节点选择连接，不支持高可用
     */
    Connection chooseConnection(InetSocketAddress node);

    void closeManager();

}
