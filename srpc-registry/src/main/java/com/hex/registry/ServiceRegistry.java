package com.hex.registry;

import java.net.InetSocketAddress;

/**
 * @author: hs
 */
public interface ServiceRegistry {

    /**
     * 注册服务到注册中心
     *
     * @param serviceName 服务名称
     * @param address     地址
     */
    void registerRpcService(String serviceName, InetSocketAddress address);
}
