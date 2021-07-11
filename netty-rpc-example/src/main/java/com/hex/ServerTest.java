package com.hex;

import com.hex.netty.annotation.RouteScan;
import com.hex.netty.config.RpcServerConfig;
import com.hex.netty.rpc.server.RpcServer;


/**
 * @author: hs
 */
@RouteScan("com.hex")
public class ServerTest {
    public static void main(String[] args) {
        // 1.自定义配置，可使用默认配置
        RpcServerConfig serverConfig = new RpcServerConfig(ServerTest.class);
        // 2.设置自定义处理器handler，类似controller
        RpcServer rpcServer = new RpcServer(serverConfig);
        // 3.启动服务端，监听端口
        rpcServer.start();

    }
}
