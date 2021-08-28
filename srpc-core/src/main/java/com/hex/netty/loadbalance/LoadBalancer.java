package com.hex.netty.loadbalance;

import com.hex.netty.node.HostAndPort;

import java.util.List;

/**
 * @author guohs
 * @date 2021/7/15
 */
public interface LoadBalancer {

    HostAndPort choose(List<HostAndPort> nodes);

}
