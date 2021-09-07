package com.hex.cluster;

import com.hex.common.annotation.RouteScan;
import com.hex.srpc.core.config.RpcServerConfig;
import com.hex.srpc.core.rpc.server.SRpcServer;


/**
 * @author: hs
 */
@RouteScan("com.hex.handler")
public class ClusterServerTest1 {
    public static void main(String[] args) {

        // 启动服务端, 需填入rpc服务端配置, 可使用默认配置, source填写有@RouteScan注解的类
        SRpcServer.builder()
                .config(new RpcServerConfig())
                .source(ClusterServerTest1.class)
                .startAtPort(8005);
    }
}
