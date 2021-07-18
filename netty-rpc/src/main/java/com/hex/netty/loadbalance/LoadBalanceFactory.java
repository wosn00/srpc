package com.hex.netty.loadbalance;

import com.hex.netty.constant.LoadBalanceRule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guohs
 * @date 2021/7/15
 */
public class LoadBalanceFactory {

    private static Map<LoadBalanceRule, LoadBalancer> loadBalanceMap;

    static {
        loadBalanceMap = new ConcurrentHashMap<>(4);
        loadBalanceMap.put(LoadBalanceRule.RANDOM, new RandomLoadBalancer());
        loadBalanceMap.put(LoadBalanceRule.ROUND, new RoundLoadBalancer());
    }

    public static LoadBalancer getLoadBalance(LoadBalanceRule rule) {
        return loadBalanceMap.get(rule);
    }
}
