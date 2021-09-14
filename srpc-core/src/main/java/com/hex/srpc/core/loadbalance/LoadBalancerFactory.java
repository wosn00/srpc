package com.hex.srpc.core.loadbalance;

import com.hex.common.constant.LoadBalanceRule;
import com.hex.srpc.core.loadbalance.impl.ConsistentHashLoadBalancer;
import com.hex.srpc.core.loadbalance.impl.RandomLoadBalancer;
import com.hex.srpc.core.loadbalance.impl.RoundLoadBalancer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guohs
 * @date 2021/7/15
 */
public class LoadBalancerFactory {

    private static Map<LoadBalanceRule, LoadBalancer> loadBalanceMap = new ConcurrentHashMap<>(4);

    static {
        loadBalanceMap.put(LoadBalanceRule.RANDOM, new RandomLoadBalancer());
        loadBalanceMap.put(LoadBalanceRule.ROUND, new RoundLoadBalancer());
        loadBalanceMap.put(LoadBalanceRule.CONSISTENT_HASH, new ConsistentHashLoadBalancer());
    }

    public static LoadBalancer getLoadBalance(LoadBalanceRule rule) {
        return loadBalanceMap.get(rule);
    }
}
