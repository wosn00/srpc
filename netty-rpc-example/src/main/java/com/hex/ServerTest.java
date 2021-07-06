package com.hex;

import com.google.common.collect.Lists;
import com.hex.handler.TestHandler;
import com.hex.netty.cmd.IHadnler;
import com.hex.netty.config.RpcServerConfig;
import com.hex.netty.rpc.server.RpcServer;

import java.util.List;

/**
 * @author: hs
 */
public class ServerTest {
    public static void main(String[] args) {
        RpcServerConfig serverConfig = new RpcServerConfig();
        // 服务端处理器
        List<IHadnler> handlers = Lists.newArrayList(new TestHandler());

        RpcServer rpcServer = new RpcServer(serverConfig, handlers);
        // 启动服务端
        rpcServer.start();

    }
}
