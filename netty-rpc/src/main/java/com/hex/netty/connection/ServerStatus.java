package com.hex.netty.connection;

import java.net.InetSocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author guohs
 * @date 2021/7/15
 */
public class ServerStatus {

    private InetSocketAddress server;
    private AtomicInteger errorTimes = new AtomicInteger(0);

    public ServerStatus(InetSocketAddress server) {
        this.server = server;
    }

    public InetSocketAddress getServer() {
        return server;
    }

    public void setServer(InetSocketAddress server) {
        this.server = server;
    }

    public int getErrorTimes() {
        return errorTimes.get();
    }

    public int errorTimesInc() {
        return errorTimes.incrementAndGet();
    }

    public boolean isAvailable() {
        return errorTimes.get() <= 3;
    }
}
