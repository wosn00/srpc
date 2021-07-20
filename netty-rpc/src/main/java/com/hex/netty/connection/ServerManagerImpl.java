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
    public synchronized void addServer(InetSocketAddress server) {
        if (isClosed.get()) {
            logger.error("serverManager closed, add server [{}] failed", server);
        }
        if (!servers.contains(server)) {
            servers.add(server);
            ConnectionPool connectionPool = connectionPoolMap.get(server);
            if (connectionPool != null) {
                connectionPool.close();
            }
            connectionPoolMap.put(server, new ConnectionPoolImpl(poolSizePerServer, server, client));
        }
    }

    @Override
    public void addServers(List<InetSocketAddress> servers) {
        for (InetSocketAddress server : servers) {
            addServer(server);
        }
    }

    @Override
    public synchronized void removeServer(InetSocketAddress server) {
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
    public InetSocketAddress[] getAllRemoteServers() {
        return servers.toArray(new InetSocketAddress[]{});
    }

    @Override
    public int getServersNum() {
        return servers.size();
    }

    @Override
    public InetSocketAddress selectServer(List<InetSocketAddress> servers) {
        // 过滤出可用的server
        List<InetSocketAddress> availableServers = servers.stream()
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
    public Connection chooseConnection(List<InetSocketAddress> addresses) {
        if (isClosed.get()) {
            logger.error("serverManager closed, choose connection failed");
        }
        InetSocketAddress address = selectServer(addresses);
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
