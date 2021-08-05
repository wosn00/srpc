package com.hex.cluster;

import com.hex.entity.WeatherData;
import com.hex.netty.config.RpcClientConfig;
import com.hex.netty.rpc.Client;
import com.hex.netty.rpc.client.RpcClient;

import java.net.InetSocketAddress;


/**
 * 集群连接模式 [多个服务端节点]
 */
public class ClusterClientTest {
    public static void main(String[] args) {
        System.out.println("---------------------客户端初始化----------------------");
        // 初始化客户端，需填入rpc客户端配置，可使用默认配置
        Client rpcClient = RpcClient.newBuilder()
                .config(new RpcClientConfig())
                .contactCluster(
                        new InetSocketAddress("127.0.0.1", 8005),
                        new InetSocketAddress("127.0.0.1", 8006),
                        new InetSocketAddress("127.0.0.1", 8007))
                .start();

        System.out.println("---------------------测试请求----------------------");

        for (int i = 0; i < 12; i++) {
            // 同步发送请求，获取响应
            WeatherData data = rpcClient.invoke("getWeather", "第" + i + "条请求内容", WeatherData.class);
            System.out.println("这是第" + i + "个响应内容:" + data);
        }

        // 异步发送请求，发送完成即返回，不阻塞等待响应结果，等回调
        rpcClient.invokeAsync("getWeather", "BeiJing",
                rpcResponse -> System.out.println("收到响应，开始执行回调方法"));
    }
}