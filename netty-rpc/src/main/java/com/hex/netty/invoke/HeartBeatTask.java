package com.hex.netty.invoke;

import com.hex.netty.connection.Connection;
import com.hex.netty.connection.ConnectionManager;
import com.hex.netty.constant.CommandType;
import com.hex.netty.protocol.Command;
import com.hex.netty.util.Util;

import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: hs
 */
public class HeartBeatTask extends TimerTask {
    private ConnectionManager connectionManager;

    private static final long HEART_BEAT_INTERVAL = TimeUnit.SECONDS.toMillis(20);

    public HeartBeatTask(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void run() {
        Connection[] allConn = this.connectionManager.getAllConn();
        for (Connection connection : allConn) {
            long lastSendTime = connection.getLastSendTime();
            if (System.currentTimeMillis() - lastSendTime > HEART_BEAT_INTERVAL) {
                Command<?> ping = new Command<>();
                ping.setSeq(Util.genSeq());
                ping.setCommandType(CommandType.HEARTBEAT.getValue());
                connection.send(ping);
            }
        }
    }
}
