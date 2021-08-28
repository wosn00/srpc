package com.hex.netty.loadbalance;


import com.hex.netty.node.HostAndPort;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author guohs
 * @date 2021/7/15
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public HostAndPort choose(List<HostAndPort> servers) {
        int randomCur = ThreadLocalRandom.current().nextInt(servers.size());
        return servers.get(randomCur);
    }
}
