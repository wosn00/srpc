package com.hex.netty.rpc.ext;

import com.google.common.base.Throwables;
import com.hex.netty.connection.Connection;
import com.hex.netty.connection.ConnectionPool;
import com.hex.netty.connection.ServerManager;
import com.hex.netty.rpc.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author: hs
 */
public class HeartBeatTask extends TimerTask {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatTask.class);
    private ServerManager serverManager;
    private Client client;

    private static final long HEART_BEAT_INTERVAL = TimeUnit.SECONDS.toMillis(10);

    public HeartBeatTask(ServerManager serverManager, Client client) {
        this.serverManager = serverManager;
        this.client = client;
    }

    @Override
    public void run() {
        // 对目前已建立的连接做心跳保活
        try {
            InetSocketAddress[] nodes = serverManager.getAllRemoteNodes();
            for (InetSocketAddress node : nodes) {
                ConnectionPool connectionPool = serverManager.getConnectionPool(node);
                for (Connection connection : connectionPool.getAllConnections()) {
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
