package com.hex;

import com.hex.entity.WeatherData;
import com.hex.netty.config.RpcClientConfig;
import com.hex.netty.rpc.Client;
import com.hex.netty.rpc.client.RpcClient;


/**
 * Hello world!
 */
public class ClientTest {
    public static void main(String[] args) {
        // 初始化客户端，需填入rpc客户端配置，可使用默认配置
        Client rpcClient = RpcClient.newBuilder()
                .config(new RpcClientConfig())
                .start();

        // 发起连接，设置初始连接数量
        rpcClient.connect("127.0.0.1", 8005, 5);

        System.out.println("---------------------测试请求----------------------");

        for (int i = 0; i < 10; i++) {
            // 同步发送请求，获取响应
            WeatherData data = rpcClient.invoke("/myRouter/getWeather", "XiaMen", WeatherData.class);
            System.out.println("这是第" + i + "个响应内容:" + data);
        }

        // 异步发送请求，发送完成即返回，不阻塞等待响应结果，等回调
        rpcClient.invokeAsync("/myRouter/getWeather", "BeiJing",
                rpcResponse -> System.out.println("收到响应，开始执行回调方法"));
    }
}
