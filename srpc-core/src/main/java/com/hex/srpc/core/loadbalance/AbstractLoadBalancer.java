package com.hex.srpc.core.loadbalance;

import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.protocol.RpcRequest;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * @author guohs
 * @date 2021/9/14
 */
public abstract class AbstractLoadBalancer implements LoadBalancer {

    @Override
    public HostAndPort selectNode(List<HostAndPort> nodes, RpcRequest request) {
        if (CollectionUtils.isEmpty(nodes)) {
            return null;
        }
        if (nodes.size() == 1) {
            return nodes.get(0);
        }
        return doSelect(nodes, request);
    }

    protected abstract HostAndPort doSelect(List<HostAndPort> nodes, RpcRequest request);
}
