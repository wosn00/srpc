package com.hex.example.registry;

import com.google.common.collect.Lists;
import com.hex.srpc.core.config.SRpcClientConfig;
import com.hex.srpc.core.rpc.Client;
import com.hex.srpc.core.rpc.client.SRpcClient;

/**
 * @author: hs
 * <p>
 * 集群连接模式[无须指定节点地址，使用注册中心获取服务地址]
 */
public class RegistryClientTest {
    public static void main(String[] args1) throws InterruptedException {
        System.out.println("---------------------客户端初始化----------------------");

        // 初始化客户端，需填入rpc客户端配置，可使用默认配置
        Client rpcClient = SRpcClient.builder()
                .config(new SRpcClientConfig())
                // 配置注册中心地址
                .configRegistry(null, Lists.newArrayList("192.168.1.2:2181"))
                .start();

        System.out.println("---------------------同步调用测试请求----------------------");

        TestRequest request = new TestRequest().setName("hs").setBody("测试请求");
        Object[] args = {request};

        for (int i = 0; i < 30; i++) {
            // 同步发送请求，指定服务名称
            TestResponse response = rpcClient.invokeWithRegistry("test2", TestResponse.class, "SRpcServerTest", args);
            System.out.println("这是第" + i + "个响应内容:" + response);
        }

        Thread.sleep(2000);
        System.exit(0);
    }
}
