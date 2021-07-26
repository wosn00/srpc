package com.hex.netty.connection;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: hs
 */
public interface ServerManager {

    void addNode(InetSocketAddress nodeAddress);

    void addCluster(List<InetSocketAddress> cluster);

    void removeNode(InetSocketAddress nodeAddress);

    InetSocketAddress[] getAllRemoteNodes();

    int getNodesSize();

    /**
     * 根据集群节点选择高可用服务，负载均衡
     */
    InetSocketAddress chooseNode(List<InetSocketAddress> cluster);

    /**
     * 根据集群节点选择出高可用连接，支持节点负载均衡，高可用性
     */
    Connection chooseConnection(List<InetSocketAddress> cluster);

    /**
     * 选择默认集群高可用连接，适用于Client只连接向一个集群的服务
     */
    Connection chooseConnection();

    /**
     * 指定server选择连接，不支持高可用，适用于连接单节点
     */
    Connection chooseConnection(InetSocketAddress node);

    void closeManager();

}
