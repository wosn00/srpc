package com.hex.netty.loadbalance;

import com.hex.netty.constant.LoadBalanceRule;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guohs
 * @date 2021/7/15
 */
public class LoadBalanceFactory {

    private Map<LoadBalanceRule, LoadBalance> loadBalanceMap = new ConcurrentHashMap<>(4);

    public void register(LoadBalance loadBalance) {
        loadBalanceMap.put(loadBalance.getRule(), loadBalance);
    }

    public LoadBalance newInstance(LoadBalanceRule rule) {
        return loadBalanceMap.get(rule);
    }
}
