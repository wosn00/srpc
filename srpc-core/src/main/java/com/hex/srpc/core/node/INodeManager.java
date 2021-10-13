package com.hex.srpc.core.node;

import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.connection.IConnection;
import com.hex.srpc.core.connection.IConnectionPool;
import com.hex.srpc.core.protocol.Command;
import com.hex.srpc.core.protocol.RpcRequest;
import com.hex.srpc.core.rpc.Client;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: hs
 * 节点管理器
 */
public interface INodeManager {

    /**
     * 添加节点
     *
     * @param node 节点
     */
    void addNode(HostAndPort node);

    /**
     * 批量添加节点
     *
     * @param nodes 节点列表
     */
    void addNodes(List<HostAndPort> nodes);

    /**
     * 移除节点管理器的某个节点
     *
     * @param node 节点
     */
    void removeNode(HostAndPort node);

    /**
     * 获取节点管理器的所有节点
     *
     * @return 节点数组
     */
    HostAndPort[] getAllRemoteNodes();

    /**
     * 获取当前节点管理器管理的节点数量
     *
     * @return 节点数量
     */
    int getNodesSize();

    /**
     * 获取指定节点的连接池
     *
     * @param node 指定节点
     * @return 连接池
     */
    IConnectionPool getConnectionPool(HostAndPort node);

    /**
     * 获取各个节点的连接数
     *
     * @return 连接数Map
     */
    Map<String, AtomicInteger> getConnectionSize();

    /**
     * 获取各个节点的状态
     *
     * @return 节点状态Mao
     */
    Map<HostAndPort, NodeStatus> getNodeStatusMap();

    /**
     * 获取节点管理器的客户端
     *
     * @return
     */
    Client getClient();

    /**
     * 根据集群节点选择高可用服务，负载均衡
     * ps:单节点的不支持高可用
     *
     * @param nodes 集群节点
     * @return 可用的节点列表
     */
    List<HostAndPort> chooseHANode(List<HostAndPort> nodes);

    /**
     * 指定节点获取连接
     *
     * @param address 指定节点
     * @return 连接
     */
    IConnection getConnectionFromPool(HostAndPort address);

    /**
     * 根据节点和指令获取连接
     *
     * @param nodes   节点
     * @param request 请求指令
     * @return 连接
     */
    IConnection chooseConnection(List<HostAndPort> nodes, RpcRequest request);

    /**
     * 是否需要排除不可用的节点（连接或请求超时/异常超过设置次数置为不可用）
     *
     * @param excludeUnAvailableNodesEnable 是否开启
     */
    void setExcludeUnAvailableNodesEnable(boolean excludeUnAvailableNodesEnable);

    /**
     * 关闭节点管理器
     */
    void closeManager();

}
