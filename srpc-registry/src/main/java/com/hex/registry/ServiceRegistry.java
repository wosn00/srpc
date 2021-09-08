package com.hex.registry;

import com.hex.common.annotation.SPI;
import com.hex.common.net.HostAndPort;

import java.util.List;

/**
 * @author: hs
 */
@SPI
public interface ServiceRegistry {

    /**
     * 注册服务到注册中心
     *
     * @param serviceName 服务名称
     * @param address     地址
     */
    void registerRpcService(String serviceName, HostAndPort address);

    /**
     * 初始化注册中心
     *
     * @param registryAddresses 注册中心地址
     */
    void initRegistry(List<String> registryAddresses);
}
