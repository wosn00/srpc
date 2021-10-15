package com.hex.example.cluster;

import com.hex.common.net.HostAndPort;
import com.hex.example.entity.TestRequest;
import com.hex.example.entity.TestResponse;
import com.hex.srpc.core.config.SRpcClientConfig;
import com.hex.srpc.core.rpc.Client;
import com.hex.srpc.core.rpc.client.SRpcClient;


/**
 * 集群连接模式 [手动指定多个服务端节点地址]
 */
public class ClusterClientTest {
    public static void main(String[] args1) throws InterruptedException {
        System.out.println("---------------------客户端初始化----------------------");

        // 初始化客户端，需填入rpc客户端配置，可使用默认配置
        Client rpcClient = SRpcClient.builder()
                .config(new SRpcClientConfig())
                .start();

        System.out.println("---------------------同步调用测试请求----------------------");
        HostAndPort node1 = new HostAndPort("127.0.0.1", 8005);
        HostAndPort node2 = new HostAndPort("127.0.0.1", 8006);
        HostAndPort node3 = new HostAndPort("127.0.0.1", 8007);
        HostAndPort[] nodes = {node1, node2, node3};

        TestRequest request = new TestRequest().setName("hs").setBody("测试请求");
        Object[] args = {request};

        for (int i = 0; i < 20; i++) {
            // 同步发送请求，获取响应
            TestResponse response = rpcClient.invoke("test2", TestResponse.class, 2, args, nodes);
            System.out.println("这是第" + i + "个响应内容:" + response);
        }

        System.out.println("---------------------异步调用测试请求----------------------");
        // 异步发送请求，发送完成即返回，不阻塞等待响应结果，等回调
        rpcClient.invokeAsync("test2",
                rpcResponse -> System.out.println("收到响应，开始执行回调方法" + rpcResponse), args, nodes);

        Thread.sleep(2000);
        System.exit(0);
    }
}
