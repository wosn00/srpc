package com.hex.netty.connection;

import com.hex.netty.rpc.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: hs
 */
public class ServerConnectionManager implements ConnectionManager {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<InetSocketAddress> servers = Collections.synchronizedList(new ArrayList<>(8));

    private Map<InetSocketAddress, ConnectionPool> connectionPoolMap = new ConcurrentHashMap<>();

    private AtomicBoolean isClosed = new AtomicBoolean(false);

    private Client client;

    public ServerConnectionManager(Client client) {
        this.client = client;
    }

    @Override
    public int size() {
        return connectionQueue.size();
    }

    @Override
    public Connection getConnection(List<InetSocketAddress> addresses) {
        return null;
    }

    @Override
    public Connection getConnection() {
        return null;
    }

}
