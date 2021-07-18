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
    public void addServer(InetSocketAddress server) {
        if (isClosed.get()) {
            logger.error("serverManager closed, add server [{}] failed", server);
        }
        if (!servers.contains(server)) {
            servers.add(server);
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
    public InetSocketAddress[] getAllServers() {
        return servers.toArray(new InetSocketAddress[]{});
    }

    @Override
    public int getServersNum() {
        return servers.size();
    }

    @Override
    public InetSocketAddress selectServer(List<InetSocketAddress> servers) {
        // 过滤掉不可用的server
        List<InetSocketAddress> availableServers = servers.stream()
                .filter(server -> serverStatusMap.get(server).isAvailable())
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
    public Connection getConnection(List<InetSocketAddress> addresses) {
        InetSocketAddress address = selectServer(addresses);

        return null;
    }

    @Override
    public Connection getConnection() {
        return null;
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
