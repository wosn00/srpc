package com.hex.netty.rpc;

import com.hex.netty.cmd.IHadnler;
import io.netty.channel.epoll.Epoll;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import org.apache.commons.lang3.SystemUtils;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author: hs
 */
public abstract class AbstractRpc {

    protected GlobalTrafficShapingHandler trafficShapingHandler;

    protected List<IHadnler> hadnlers;

    protected boolean useEpoll() {
        return SystemUtils.IS_OS_LINUX && Epoll.isAvailable();
    }

    protected void buildTrafficMonitor(ScheduledExecutorService executor, Boolean trafficMonitorEnable, Long maxReadSpeed, Long maxWriteSpeed) {
        if (trafficMonitorEnable != null && trafficMonitorEnable) {
            if (maxReadSpeed == null) {
                maxReadSpeed = 0L;
            }
            if (maxWriteSpeed == null) {
                maxWriteSpeed = 0L;
            }
            trafficShapingHandler = new GlobalTrafficShapingHandler(executor, maxWriteSpeed, maxReadSpeed);
        }
    }
}
