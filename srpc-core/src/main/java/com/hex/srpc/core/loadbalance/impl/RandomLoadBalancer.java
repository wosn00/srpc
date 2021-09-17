package com.hex.srpc.core.loadbalance.impl;


import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.loadbalance.AbstractLoadBalancer;
import com.hex.srpc.core.protocol.Command;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author guohs
 * @date 2021/7/15
 * <p>
 * 随机选取策略
 */
public class RandomLoadBalancer extends AbstractLoadBalancer {

    @Override
    protected HostAndPort doSelect(List<HostAndPort> nodes, Command<?> command) {
        int randomCur = ThreadLocalRandom.current().nextInt(nodes.size());
        return nodes.get(randomCur);
    }
}
