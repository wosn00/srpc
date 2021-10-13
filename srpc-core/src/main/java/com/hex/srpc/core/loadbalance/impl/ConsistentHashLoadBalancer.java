package com.hex.srpc.core.loadbalance.impl;

import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.loadbalance.AbstractLoadBalancer;
import com.hex.srpc.core.protocol.RpcRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author guohs
 * @date 2021/9/14
 * <p>
 * 一致性hash策略，根据cmd和参数计算hash选取
 */
public class ConsistentHashLoadBalancer extends AbstractLoadBalancer {
    private final ConcurrentHashMap<String, ConsistentHashSelector> selectors = new ConcurrentHashMap<>();

    @Override
    protected HostAndPort doSelect(List<HostAndPort> nodes, RpcRequest request) {
        int identityHashCode = System.identityHashCode(nodes);
        String mapping = request.getMapping();
        ConsistentHashSelector selector = selectors.get(mapping);
        if (selector == null || selector.identityHashCode != identityHashCode) {
            selectors.put(mapping, new ConsistentHashSelector(nodes, 160, identityHashCode));
            selector = selectors.get(mapping);
        }
        return selector.select(Arrays.stream(request.getArgs()));
    }

    static class ConsistentHashSelector {
        private final TreeMap<Long, HostAndPort> virtualInvokers;

        private final int identityHashCode;

        ConsistentHashSelector(List<HostAndPort> nodes, int replicaNumber, int identityHashCode) {
            this.virtualInvokers = new TreeMap<>();
            this.identityHashCode = identityHashCode;

            for (HostAndPort node : nodes) {
                for (int i = 0; i < replicaNumber / 4; i++) {
                    byte[] digest = md5(node.toString() + i);
                    for (int h = 0; h < 4; h++) {
                        long m = hash(digest, h);
                        virtualInvokers.put(m, node);
                    }
                }
            }
        }

        static byte[] md5(String key) {
            MessageDigest md;
            try {
                md = MessageDigest.getInstance("MD5");
                byte[] bytes = key.getBytes(StandardCharsets.UTF_8);
                md.update(bytes);
            } catch (NoSuchAlgorithmException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }

            return md.digest();
        }

        static long hash(byte[] digest, int idx) {
            return ((long) (digest[3 + idx * 4] & 255) << 24 | (long) (digest[2 + idx * 4] & 255) << 16 | (long) (digest[1 + idx * 4] & 255) << 8 | (long) (digest[idx * 4] & 255)) & 4294967295L;
        }

        HostAndPort select(Object rpcServiceKey) {
            byte[] digest = md5(rpcServiceKey.toString());
            return selectForKey(hash(digest, 0));
        }

        HostAndPort selectForKey(long hashCode) {
            Map.Entry<Long, HostAndPort> entry = virtualInvokers.tailMap(hashCode, true).firstEntry();

            if (entry == null) {
                entry = virtualInvokers.firstEntry();
            }

            return entry.getValue();
        }
    }
}
