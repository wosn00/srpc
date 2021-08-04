package com.hex.netty.connection;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * @author: hs
 * <p>
 * 服务健康状态检查
 */
public class ServerHealthCheckTask implements Runnable {

    private ServerManager serverManager;

    public ServerHealthCheckTask(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public void run() {
        Map<InetSocketAddress, ServerStatus> serverStatusMap = serverManager.getServerStatusMap();
        for (ServerStatus serverStatus : serverStatusMap.values()) {
            if (!serverStatus.isAvailable()) {
                //发送心跳包探测
                boolean avaiable = serverManager.getClient().sendHeartBeat(serverStatus.getServer());
                if (Boolean.TRUE.equals(avaiable)) {
                    //心跳成功则置0
                    serverStatusMap.remove(serverStatus.getServer());
                }
            }
        }


    }
}
