package com.hex.netty.loadbalance;

import org.apache.commons.lang3.StringUtils;

import java.net.InetSocketAddress;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author guohs
 * @date 2021/7/15
 * <p>
 * 轮询策略
 */
public class RoundLoadBalancer implements LoadBalancer {

    private final Map<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();

    @Override

    public InetSocketAddress choose(List<InetSocketAddress> servers) {
        servers.sort(Comparator.comparing(InetSocketAddress::toString));
        String serverJoin = StringUtils.join(servers.toArray(), ";");
        AtomicInteger counter = counterMap.get(serverJoin);
        if (counter == null) {
            synchronized (counterMap) {
                if ((counter = counterMap.get(serverJoin)) == null) {
                    counter = new AtomicInteger(0);
                    counterMap.put(serverJoin, counter);
                }
            }
        }
        return servers.get(incrementAndGetModulo(servers.size(), counter));
    }

    private int incrementAndGetModulo(int modulo, AtomicInteger counter) {
        for (; ; ) {
            int current = counter.get();
            int next = (current + 1) % modulo;
            if (counter.compareAndSet(current, next)) {
                return next;
            }
        }
    }
}