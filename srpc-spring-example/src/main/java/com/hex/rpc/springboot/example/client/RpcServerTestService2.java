package com.hex.rpc.springboot.example.client;

import com.hex.rpc.sping.annotation.SRpcClient;

/**
 * @author: hs
 * <p>
 * 指定服务名称，从注册中心获取节点地址[需配置注册中心]
 */
@SRpcClient(serviceName = "test-application", retryTimes = 2)
public interface RpcServerTestService2 {

    public void test();
}
