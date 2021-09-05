package com.hex;

import com.hex.common.annotation.RouteScan;
import com.hex.srpc.core.config.RpcServerConfig;
import com.hex.srpc.core.rpc.server.SrpcServer;


/**
 * @author: hs
 */
@RouteScan
public class ServerTest {
    public static void main(String[] args) {

        // 启动服务端, 需填入rpc服务端配置, 可使用默认配置, source填写有@RouteScan注解的类
        SrpcServer.builder()
                .config(new RpcServerConfig())
                .source(ServerTest.class)
                .startAtPort(8005);
    }
}
