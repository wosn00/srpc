package com.hex.discovery;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: hs
 */
public interface ServiceDiscovery {

    /**
     * 获取rpc注册中心服务名称
     *
     * @param serviceName 服务名称
     * @return 注册中心地址
     */
    List<InetSocketAddress> getRpcServiceAddress(String serviceName);
}
