package com.hex.example.direct;

import com.hex.common.net.HostAndPort;
import com.hex.example.entity.TestRequest;
import com.hex.example.entity.TestResponse;
import com.hex.srpc.core.config.SRpcClientConfig;
import com.hex.srpc.core.rpc.Client;
import com.hex.srpc.core.rpc.client.SRpcClient;


/**
 * 单机连接模式 [连接单个服务端节点]
 */
public class ClientTest {
    public static void main(String[] args1) throws InterruptedException {
        System.out.println("---------------------客户端初始化----------------------");

        // 初始化客户端，需填入rpc客户端配置，可使用默认配置
        Client rpcClient = SRpcClient.builder()
                .config(new SRpcClientConfig())
                .start();

        System.out.println("---------------------同步调用测试请求----------------------");

        TestRequest request = new TestRequest().setName("hs").setBody("测试请求");
        Object[] args = {request};

        HostAndPort node = HostAndPort.from("127.0.0.1:8005");
        for (int i = 0; i < 30; i++) {
            // 同步发送请求，获取响应
            TestResponse response = rpcClient.invoke("test2", TestResponse.class, args, node);
            System.out.println("这是第" + i + "个响应内容:" + response);
        }

        rpcClient.invoke("test3", Void.TYPE, null, node);

        System.out.println("---------------------异步调用测试请求----------------------");
        // 异步发送请求，发送完成即返回，不阻塞等待响应结果，等回调
        rpcClient.invokeAsync("test2",
                rpcResponse -> System.out.println("收到响应，开始执行回调方法" + rpcResponse), args, node);

        Thread.sleep(2000);
        System.exit(0);
    }
}
