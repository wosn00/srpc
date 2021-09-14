package com.hex.srpc.core.node;

import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.connection.IConnection;
import com.hex.srpc.core.connection.IConnectionPool;
import com.hex.srpc.core.rpc.Client;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: hs
 * 节点管理器
 */
public interface INodeManager {

    void addNode(HostAndPort node);

    void addNodes(List<HostAndPort> nodes);

    void removeNode(HostAndPort node);

    HostAndPort[] getAllRemoteNodes();

    int getNodesSize();

    IConnectionPool getConnectionPool(HostAndPort node);

    Map<String, AtomicInteger> getConnectionSize();

    Map<HostAndPort, NodeStatus> getNodeStatusMap();

    Client getClient();

    /**
     * 根据集群节点选择高可用服务，负载均衡
     * ps:单节点的不支持高可用
     */
    List<HostAndPort> chooseHANode(List<HostAndPort> nodes);

    /**
     * 根据节点获取连接
     */
    IConnection getConnectionFromPool(HostAndPort address);


    void closeManager();

}
