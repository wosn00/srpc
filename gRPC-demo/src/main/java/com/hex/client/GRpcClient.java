package com.hex.client;

import com.hex.proto.TestRequest;
import com.hex.proto.TestResponse;
import com.hex.proto.TestRpcGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * @author guohs
 * @date 2021/7/14
 */
public class GRpcClient {

    private static final String serverHost = "127.0.0.1";
    private static final int serverPort = 8008;

    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress(serverHost, serverPort)
                .usePlaintext()
                .build();
        try {
            TestRpcGrpc.TestRpcBlockingStub rpcService = TestRpcGrpc.newBlockingStub(managedChannel);
            TestRequest request = TestRequest
                    .newBuilder()
                    .setName("hex")
                    .build();
            // rpc调用方法
            TestResponse response = rpcService.getData(request);
            System.out.println(response.getMessage());
        } finally {
            managedChannel.shutdown();
        }
    }
}
