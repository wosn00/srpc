package com.hex.srpc.core.loadbalance;

import com.hex.srpc.core.node.HostAndPort;
import org.apache.commons.lang3.StringUtils;

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

    public HostAndPort choose(List<HostAndPort> nodes) {
        nodes.sort(Comparator.comparing(HostAndPort::toString));
        String serverJoin = StringUtils.join(nodes.toArray(), ";");
        AtomicInteger counter = counterMap.get(serverJoin);
        if (counter == null) {
            synchronized (counterMap) {
                if ((counter = counterMap.get(serverJoin)) == null) {
                    counter = new AtomicInteger(0);
                    counterMap.put(serverJoin, counter);
                }
            }
        }
        return nodes.get(incrementAndGetModulo(nodes.size(), counter));
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
