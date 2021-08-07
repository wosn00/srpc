package com.hex.netty.loadbalance;


import com.hex.netty.node.HostAndPort;

import java.util.List;

/**
 * @author guohs
 * @date 2021/7/15
 */
public class RandomLoadBalancer implements LoadBalancer {

    @Override
    public HostAndPort choose(List<HostAndPort> servers) {
        return null;
    }
}
