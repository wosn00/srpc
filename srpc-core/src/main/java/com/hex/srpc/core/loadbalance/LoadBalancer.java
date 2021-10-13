package com.hex.srpc.core.loadbalance;

import com.hex.common.annotation.SPI;
import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.protocol.RpcRequest;

import java.util.List;

/**
 * @author guohs
 * @date 2021/7/15
 */
@SPI
public interface LoadBalancer {

    HostAndPort selectNode(List<HostAndPort> nodes, RpcRequest request);

}
