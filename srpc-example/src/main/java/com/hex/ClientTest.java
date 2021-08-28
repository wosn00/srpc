package com.hex;

import com.hex.entity.TestRequest;
import com.hex.entity.TestResponse;
import com.hex.netty.config.RpcClientConfig;
import com.hex.netty.node.HostAndPort;
import com.hex.netty.rpc.Client;
import com.hex.netty.rpc.client.RpcClient;


/**
 * 单机连接模式 [单个服务端节点]
 */
public class ClientTest {
    public static void main(String[] args) {
        System.out.println("---------------------客户端初始化----------------------");

        // 初始化客户端，需填入rpc客户端配置，可使用默认配置
        Client rpcClient = RpcClient.builder()
                .config(new RpcClientConfig())
                .contact(new HostAndPort("127.0.0.1", 8005))
                .start();

        System.out.println("---------------------同步调用测试请求----------------------");

        TestRequest request = new TestRequest().setName("hs").setBody("测试请求");

        for (int i = 0; i < 10; i++) {
            // 同步发送请求，获取响应
            TestResponse response = rpcClient.invoke("test2", request, TestResponse.class);
            System.out.println("这是第" + i + "个响应内容:" + response);
        }

        System.out.println("---------------------异步调用测试请求----------------------");
        // 异步发送请求，发送完成即返回，不阻塞等待响应结果，等回调
        rpcClient.invokeAsync("test2", request,
                rpcResponse -> System.out.println("收到响应，开始执行回调方法" + rpcResponse));
    }
}