package com.hex;

import com.hex.netty.config.RpcClientConfig;
import com.hex.netty.protocol.RpcRequest;
import com.hex.netty.protocol.RpcResponse;
import com.hex.netty.rpc.client.RpcClient;


/**
 * Hello world!
 */
public class ClientTest {
    public static void main(String[] args) {
        // 1.自定义配置，可使用默认配置
        RpcClientConfig clientConfig = new RpcClientConfig();
        // 设置自定义处理器handler，类似controller
        RpcClient rpcClient = new RpcClient(clientConfig);

        // 2.启动客户端
        rpcClient.start();
        // 3.发起连接，设置初始连接数量
        rpcClient.connect("127.0.0.1", 8008, 5);

        System.out.println("---------------------测试请求----------------------");

        for (int i = 0; i < 10; i++) {
            // 4.同步发送请求，获取响应
            RpcResponse response = rpcClient.invoke("/myRouter/test1", "这是请求内容");
            System.out.println("这是第" + i + "个响应内容:" + response);
        }

        // 4.异步发送请求
        rpcClient.invokeAsync("/test/cmd", "这是异步请求内容",
                rpcResponse -> System.out.println("收到响应，开始执行回调方法"));

    }
}
