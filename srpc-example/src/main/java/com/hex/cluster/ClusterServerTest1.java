package com.hex.cluster;

import com.hex.common.annotation.SRpcScan;
import com.hex.srpc.core.config.SRpcServerConfig;
import com.hex.srpc.core.rpc.server.SRpcServer;


/**
 * @author: hs
 */
@SRpcScan("com.hex.handler")
public class ClusterServerTest1 {
    public static void main(String[] args) {

        // 启动服务端, 需填入rpc服务端配置, 可使用默认配置, source填写有@RouteScan注解的类
        SRpcServer.builder()
                .serverConfig(new SRpcServerConfig())
                .sourceClass(ClusterServerTest1.class)
                .port(8005)
                .start();
    }
}
