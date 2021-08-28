package com.hex.netty.node;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author guohs
 * @date 2021/7/15
 */
public class NodeStatus {

    private HostAndPort node;
    private AtomicInteger errorTimes = new AtomicInteger(0);

    public NodeStatus(HostAndPort node) {
        this.node = node;
    }

    public HostAndPort getNode() {
        return node;
    }

    public void setNode(HostAndPort node) {
        this.node = node;
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
