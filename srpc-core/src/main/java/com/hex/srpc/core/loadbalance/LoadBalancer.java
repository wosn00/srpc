package com.hex.srpc.core.loadbalance;

import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.protocol.Command;

import java.util.List;

/**
 * @author guohs
 * @date 2021/7/15
 */
public interface LoadBalancer {

    HostAndPort selectNode(List<HostAndPort> nodes, Command<?> command);

}
