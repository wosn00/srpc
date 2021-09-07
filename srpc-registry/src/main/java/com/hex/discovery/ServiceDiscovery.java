package com.hex.discovery;

import com.hex.common.annotation.SPI;
import com.hex.common.net.HostAndPort;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: hs
 */
@SPI
public interface ServiceDiscovery {

    /**
     * 获取rpc注册中心服务名称
     *
     * @param registryAddresses 注册中心地址
     * @param serviceName       服务名称
     * @return 注册中心地址
     */
    List<HostAndPort> discoverRpcServiceAddress(List<String> registryAddresses, String serviceName);
}
