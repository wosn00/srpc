package com.hex.example.cluster;

import com.hex.common.annotation.SRpcScan;
import com.hex.srpc.core.config.SRpcServerConfig;
import com.hex.srpc.core.rpc.server.SRpcServer;


/**
 * @author: hs
 * 集群连接模式 [服务节点3]
 */
@SRpcScan("com.hex.example")
public class ClusterServerTest3 {
    public static void main(String[] args) {

        // 启动服务端, 需填入rpc服务端配置, 可使用默认配置, source填写有@RouteScan注解的类
        SRpcServer.builder()
                .serverConfig(new SRpcServerConfig())
                .sourceClass(ClusterServerTest3.class)
                .port(8007)
                .start();
    }
}
