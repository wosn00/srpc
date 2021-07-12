package com.hex;

import com.hex.netty.annotation.RouteScan;
import com.hex.netty.config.RpcServerConfig;
import com.hex.netty.rpc.server.RpcServer;


/**
 * @author: hs
 */
@RouteScan
public class ServerTest {
    public static void main(String[] args) {

        // 启动服务端, 需填入rpc服务端配置, 可使用默认配置, source填写有@RouteScan注解的类
        RpcServer.newBuilder()
                .config(new RpcServerConfig())
                .source(ServerTest.class)
                .startAtPort(8005);
    }
}
