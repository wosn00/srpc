package com.hex.registry;

import com.hex.common.annotation.SPI;
import com.hex.common.net.HostAndPort;

import java.util.List;

/**
 * @author: hs
 */
@SPI
public interface ServiceDiscover {

    /**
     * 初始化注册中心
     *
     * @param registryAddresses 注册中心地址
     */
    void initRegistry(List<String> registryAddresses);

    /**
     * 获取rpc注册中心服务名称
     *
     * @param serviceName 服务名称
     * @return 注册中心地址
     */
    List<HostAndPort> discoverRpcServiceAddress(String serviceName);
}
