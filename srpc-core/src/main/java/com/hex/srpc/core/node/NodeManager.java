package com.hex.srpc.core.node;

import com.hex.srpc.core.connection.ConnectionPool;
import com.hex.srpc.core.connection.IConnection;
import com.hex.srpc.core.connection.IConnectionPool;
import com.hex.common.constant.LoadBalanceRule;
import com.hex.common.exception.RpcException;
import com.hex.srpc.core.loadbalance.LoadBalanceFactory;
import com.hex.srpc.core.loadbalance.LoadBalancer;
import com.hex.srpc.core.rpc.Client;
import com.hex.srpc.core.rpc.client.SrpcClient;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author: hs
 * 维护各个server连接
 */
public class NodeManager implements INodeManager {
    private static final Logger logger = LoggerFactory.getLogger(NodeManager.class);

    private boolean isClient;
    private final List<HostAndPort> servers = new CopyOnWriteArrayList<>();
    private final Map<HostAndPort, IConnectionPool> connectionPoolMap = new ConcurrentHashMap<>();
    private static final Map<HostAndPort, NodeStatus> nodeStatusMap = new ConcurrentHashMap<>();
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private SrpcClient client;
    private int poolSizePerServer;
    private LoadBalanceRule loadBalanceRule;

    public NodeManager(boolean isClient) {
        this.isClient = isClient;
    }

    public NodeManager(boolean isClient, SrpcClient client, int poolSizePerServer, LoadBalanceRule loadBalanceRule) {
        this.isClient = isClient;
        this.client = client;
        this.poolSizePerServer = poolSizePerServer;
        this.loadBalanceRule = loadBalanceRule;
    }

    @Override
    public synchronized void addNode(HostAndPort node) {
        if (isClosed.get()) {
            logger.error("nodeManager closed, add server {} failed", node);
        }
        if (!servers.contains(node)) {
            servers.add(node);
            IConnectionPool connectionPool = connectionPoolMap.get(node);
            if (connectionPool != null) {
                connectionPool.close();
            }
            connectionPoolMap.put(node, new ConnectionPool(poolSizePerServer, node, client));
        }
    }

    @Override
    public void addCluster(List<HostAndPort> servers) {
        for (HostAndPort server : servers) {
            addNode(server);
        }
    }

    @Override
    public synchronized void removeNode(HostAndPort server) {
        servers.remove(server);
        IConnectionPool connectionPool = connectionPoolMap.get(server);
        if (connectionPool != null) {
            connectionPool.close();
            connectionPoolMap.remove(server);
        }
        NodeStatus nodeStatus = nodeStatusMap.get(server);
        if (nodeStatus != null) {
            nodeStatusMap.remove(server);
        }
    }

    @Override
    public HostAndPort[] getAllRemoteNodes() {
        return servers.toArray(new HostAndPort[]{});
    }

    @Override
    public int getNodesSize() {
        return servers.size();
    }

    @Override
    public IConnectionPool getConnectionPool(HostAndPort node) {
        return connectionPoolMap.get(node);
    }

    @Override
    public Map<String, AtomicInteger> getConnectionSize() {
        if (CollectionUtils.isEmpty(servers)) {
            return Collections.emptyMap();
        }
        Map<String, AtomicInteger> connectionSizeMap = new HashMap<>();
        for (HostAndPort server : servers) {
            AtomicInteger counter = connectionSizeMap.computeIfAbsent(server.getHost(),
                    k -> new AtomicInteger(0));
            counter.addAndGet(connectionPoolMap.get(server).size());
        }
        return connectionSizeMap;
    }

    @Override
    public HostAndPort chooseHANode(List<HostAndPort> cluster) {
        // 过滤出可用的server
        List<HostAndPort> availableServers = cluster.stream()
                .filter(server -> nodeStatusMap.get(server) == null || nodeStatusMap.get(server).isAvailable())
                .collect(Collectors.toList());
        if (availableServers.isEmpty()) {
            throw new RpcException("no available server");
        }
        LoadBalancer loadBalancer = LoadBalanceFactory.getLoadBalance(loadBalanceRule);
        // 负载均衡
        return loadBalancer.choose(availableServers);
    }

    @Override
    public IConnection chooseHAConnection(List<HostAndPort> cluster) {
        if (isClosed.get()) {
            logger.error("nodeManager closed, choose connection failed");
        }
        HostAndPort address = chooseHANode(cluster);
        return getConnectionFromPool(address);
    }

    @Override
    public IConnection chooseConnection(HostAndPort address) {
        if (isClosed.get()) {
            logger.error("nodeManager closed, choose connection failed");
        }
        return getConnectionFromPool(address);
    }

    @Override
    public void closeManager() {
        if (isClosed.compareAndSet(false, true)) {
            for (IConnectionPool connectionPool : connectionPoolMap.values()) {
                connectionPool.close();
            }
            servers.clear();
            connectionPoolMap.clear();
        }
    }

    private IConnection getConnectionFromPool(HostAndPort address) {
        IConnectionPool connectionPool = connectionPoolMap.get(address);
        if (connectionPool == null) {
            throw new RpcException("no connectionPool exist, try to add cluster or node first");
        }
        return connectionPool.getConnection();
    }

    @Override
    public Map<HostAndPort, NodeStatus> getNodeStatusMap() {
        return nodeStatusMap;
    }

    @Override
    public Client getClient() {
        return client;
    }

    public static void serverError(HostAndPort server) {
        NodeStatus nodeStatus = nodeStatusMap.get(server);
        if (nodeStatus == null) {
            synchronized (nodeStatusMap) {
                if ((nodeStatus = nodeStatusMap.get(server)) == null) {
                    nodeStatus = new NodeStatus(server);
                    nodeStatusMap.put(server, nodeStatus);
                }
            }
        }
        nodeStatus.errorTimesInc();
    }
}
