package com.hex.netty.rpc;

import io.netty.channel.epoll.Epoll;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author: hs
 */
public abstract class AbstractRpc {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractRpc.class);

    protected GlobalTrafficShapingHandler trafficShapingHandler;

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
