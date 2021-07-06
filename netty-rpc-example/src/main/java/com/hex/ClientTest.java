package com.hex;

import com.google.common.collect.Lists;
import com.hex.handler.TestHandler;
import com.hex.netty.cmd.IHadnler;
import com.hex.netty.config.RpcClientConfig;
import com.hex.netty.protocol.RpcRequest;
import com.hex.netty.protocol.RpcResponse;
import com.hex.netty.rpc.client.RpcClient;

import java.util.List;

/**
 * Hello world!
 */
public class ClientTest {
    public static void main(String[] args) {
        RpcClientConfig clientConfig = new RpcClientConfig();
        List<IHadnler> handlers = Lists.newArrayList(new TestHandler());
        RpcClient rpcClient = new RpcClient(clientConfig, handlers);

        // 初始化客户端
        rpcClient.start();
        // 发起连接
        rpcClient.connect("127.0.0.1", 8500);

        // 构造请求
        RpcRequest<String> request = new RpcRequest<>();
        request.setCmd("/test/cmd");
        request.setBody("这是请求内容");

        // 同步发送请求，获取响应
        RpcResponse response = rpcClient.invoke(request);
        System.out.println(response);

        // 异步发送请求
        rpcClient.invokeAsync(request, rpcResponse -> System.out.println("收到响应，开始执行回调方法"));


    }
}
