package com.hex.netty.connection;

import com.hex.netty.constant.LoadBalanceRule;
import com.hex.netty.loadbalance.LoadBalancer;
import com.hex.netty.loadbalance.LoadBalanceFactory;
import com.hex.netty.rpc.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author: hs
 * 维护各个server连接
 */
public class ServerManagerImpl implements ServerManager {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final List<InetSocketAddress> servers = new CopyOnWriteArrayList<>();
    private final Map<InetSocketAddress, ConnectionPool> connectionPoolMap = new ConcurrentHashMap<>();
    private static final Map<InetSocketAddress, ServerStatus> serverStatusMap = new ConcurrentHashMap<>();
    private final AtomicBoolean isClosed = new AtomicBoolean(false);
    private Client client;
    private int poolSizePerServer;
    private LoadBalanceRule loadBalanceRule;

    public ServerManagerImpl(Client client, int poolSizePerServer, LoadBalanceRule loadBalanceRule) {
        this.client = client;
        this.poolSizePerServer = poolSizePerServer;
        this.loadBalanceRule = loadBalanceRule;
    }

    @Override
    public synchronized void addNode(InetSocketAddress node) {
        if (isClosed.get()) {
            logger.error("serverManager closed, add server [{}] failed", node);
        }
        if (!servers.contains(node)) {
            servers.add(node);
            ConnectionPool connectionPool = connectionPoolMap.get(node);
            if (connectionPool != null) {
                connectionPool.close();
            }
            connectionPoolMap.put(node, new ConnectionPoolImpl(poolSizePerServer, node, client));
        }
    }

    @Override
    public void addCluster(List<InetSocketAddress> servers) {
        for (InetSocketAddress server : servers) {
            addNode(server);
        }
    }

    @Override
    public synchronized void removeNode(InetSocketAddress server) {
        servers.remove(server);
        ConnectionPool connectionPool = connectionPoolMap.get(server);
        if (connectionPool != null) {
            connectionPool.close();
            connectionPoolMap.remove(server);
        }
        ServerStatus serverStatus = serverStatusMap.get(server);
        if (serverStatus != null) {
            serverStatusMap.remove(server);
        }
    }

    @Override
    public InetSocketAddress[] getAllRemoteNodes() {
        return servers.toArray(new InetSocketAddress[]{});
    }

    @Override
    public int getNodesSize() {
        return servers.size();
    }

    @Override
    public InetSocketAddress chooseNode(List<InetSocketAddress> cluster) {
        // 过滤出可用的server
        List<InetSocketAddress> availableServers = cluster.stream()
                .filter(server -> serverStatusMap.get(server) == null || serverStatusMap.get(server).isAvailable())
                .collect(Collectors.toList());
        if (availableServers.isEmpty()) {
            logger.error("no available server");
            return null;
        }
        LoadBalancer loadBalancer = LoadBalanceFactory.getLoadBalance(loadBalanceRule);
        // 负载均衡
        return loadBalancer.choose(availableServers);
    }

    @Override
    public Connection chooseConnection(List<InetSocketAddress> cluster) {
        if (isClosed.get()) {
            logger.error("serverManager closed, choose connection failed");
        }
        InetSocketAddress address = chooseNode(cluster);
        return getConnectionFromPool(address);
    }

    @Override
    public Connection chooseConnection(InetSocketAddress address) {
        if (isClosed.get()) {
            logger.error("serverManager closed, choose connection failed");
        }
        return getConnectionFromPool(address);
    }


    @Override
    public Connection chooseConnection() {
        return chooseConnection(servers);
    }

    @Override
    public void closeManager() {
        if (isClosed.compareAndSet(false, true)) {
            for (ConnectionPool connectionPool : connectionPoolMap.values()) {
                connectionPool.close();
            }
            servers.clear();
            connectionPoolMap.clear();
        }
    }

    private Connection getConnectionFromPool(InetSocketAddress address) {
        ConnectionPool connectionPool = connectionPoolMap.get(address);
        if (connectionPool == null) {
            synchronized (address.toString().intern()) {
                if ((connectionPool = connectionPoolMap.get(address)) == null) {
                    connectionPool = new ConnectionPoolImpl(poolSizePerServer, address, client);
                    connectionPoolMap.put(address, connectionPool);
                }
            }
        }
        return connectionPool.getConnection();
    }

    public static void serverError(InetSocketAddress server) {
        ServerStatus serverStatus = serverStatusMap.get(server);
        if (serverStatus == null) {
            synchronized (serverStatusMap) {
                if ((serverStatus = serverStatusMap.get(server)) == null) {
                    serverStatus = new ServerStatus(server);
                    serverStatusMap.put(server, serverStatus);
                }
            }
        }
        serverStatus.errorTimesInc();
    }
}
