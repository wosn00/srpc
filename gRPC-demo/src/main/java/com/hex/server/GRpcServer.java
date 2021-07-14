package com.hex.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;

/**
 * @author guohs
 * @date 2021/7/14
 */
public class GRpcServer {
    private static int port = 8008;

    public static void main(String[] args) throws Exception {
        Server server = ServerBuilder.
                forPort(port)
                .addService(new RpcServerImpl())
                .build()
                .start();
        System.out.println("gRPC服务端启动成功, 端口=" + port);

        server.awaitTermination();
    }


}
