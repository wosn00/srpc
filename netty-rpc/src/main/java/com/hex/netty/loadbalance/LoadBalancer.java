package com.hex.netty.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author guohs
 * @date 2021/7/15
 */
public interface LoadBalancer {

    InetSocketAddress choose(List<InetSocketAddress> servers);

}
