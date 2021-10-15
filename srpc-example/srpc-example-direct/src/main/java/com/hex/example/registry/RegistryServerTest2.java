package com.hex.example.registry;

import com.google.common.collect.Lists;
import com.hex.common.annotation.SRpcScan;
import com.hex.srpc.core.config.SRpcServerConfig;
import com.hex.srpc.core.rpc.server.SRpcServer;

/**
 * @author: hs
 * <p>
 * 集群服务2[服务会注册到注册中心, 需要先启动zookeeper或对应的注册中心]
 */
@SRpcScan("com.hex.example")
public class RegistryServerTest2 {

    public static void main(String[] args) {

        // 启动服务端, 需填入rpc服务端配置, 可使用默认配置, source填写有@RouteScan注解的类
        SRpcServer.builder()
                .serverConfig(new SRpcServerConfig())
                .sourceClass(RegistryServerTest2.class)
                // 配置注册中心地址, 需要注册到注册中心上的服务名称
                .configRegistry(null, Lists.newArrayList("192.168.1.2:2181"), "SRpcServerTest")
                .port(8006)
                .start();
    }
}
