package com.hex.netty.rpc.ext;

import com.hex.netty.connection.Connection;
import com.hex.netty.connection.ServerManager;
import com.hex.netty.constant.CommandType;
import com.hex.netty.protocol.Command;
import com.hex.netty.util.Util;

import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * @author: hs
 */
public class HeartBeatTask extends TimerTask {
    private ServerManager serverManager;

    private static final long HEART_BEAT_INTERVAL = TimeUnit.SECONDS.toMillis(10);

    public HeartBeatTask(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    @Override
    public void run() {
        Connection[] allConn = this.serverManager.getAllConn();
        for (Connection connection : allConn) {
            long lastSendTime = connection.getLastSendTime();
            if (System.currentTimeMillis() - lastSendTime > HEART_BEAT_INTERVAL) {
                Command<String> ping = new Command<>();
                ping.setSeq(Util.genSeq());
                ping.setCommandType(CommandType.HEARTBEAT.getValue());
                ping.setBody("ping");
                connection.send(ping);
            }
        }
    }
}
