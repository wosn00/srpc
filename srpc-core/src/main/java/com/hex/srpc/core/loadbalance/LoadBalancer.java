package com.hex.srpc.core.loadbalance;

import com.hex.srpc.core.node.HostAndPort;

import java.util.List;

/**
 * @author guohs
 * @date 2021/7/15
 */
public interface LoadBalancer {

    HostAndPort choose(List<HostAndPort> nodes);

}
