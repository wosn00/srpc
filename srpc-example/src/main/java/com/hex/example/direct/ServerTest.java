package com.hex.example.direct;

import com.hex.common.annotation.SRpcScan;
import com.hex.srpc.core.config.SRpcServerConfig;
import com.hex.srpc.core.rpc.server.SRpcServer;


/**
 * @author: hs
 * <p>
 * 单机连接模式 [单个服务端节点]
 */
@SRpcScan("com.hex")
public class ServerTest {
    public static void main(String[] args) {

        // 启动服务端, 需填入rpc服务端配置, 可使用默认配置, source填写有@RouteScan注解的类
        SRpcServer.builder()
                .serverConfig(new SRpcServerConfig())
                .sourceClass(ServerTest.class)
                .port(8005)
                .start();
    }
}
