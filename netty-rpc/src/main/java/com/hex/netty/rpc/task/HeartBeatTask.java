package com.hex.netty.rpc.task;

import com.google.common.base.Throwables;
import com.hex.netty.connection.IConnection;
import com.hex.netty.connection.IConnectionPool;
import com.hex.netty.node.HostAndPort;
import com.hex.netty.node.INodeManager;
import com.hex.netty.rpc.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author: hs
 */
public class HeartBeatTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatTask.class);
    private INodeManager nodeManager;
    private Client client;

    private static final long HEART_BEAT_INTERVAL = TimeUnit.SECONDS.toMillis(10);

    public HeartBeatTask(INodeManager nodeManager, Client client) {
        this.nodeManager = nodeManager;
        this.client = client;
    }

    @Override
    public void run() {
        // 对目前已建立的连接做心跳保活
        try {
            HostAndPort[] nodes = nodeManager.getAllRemoteNodes();
            for (HostAndPort node : nodes) {
                IConnectionPool connectionPool = nodeManager.getConnectionPool(node);
                for (IConnection connection : connectionPool.getAllConnections()) {
                    long lastSendTime = connection.getLastSendTime();
                    if (System.currentTimeMillis() - lastSendTime > HEART_BEAT_INTERVAL) {
                        client.sendHeartBeat(connection);
                    }
                }
            }
        } catch (Exception e) {
            logger.error(Throwables.getStackTraceAsString(e));
        }
    }
}
