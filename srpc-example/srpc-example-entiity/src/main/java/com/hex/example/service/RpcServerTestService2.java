package com.hex.example.service;


import com.hex.common.annotation.SRpcClient;

/**
 * @author: hs
 * <p>
 * 指定服务名称，从注册中心获取节点地址[需配置注册中心]
 */
@SRpcClient(serviceName = "test-application", retryTimes = 2)
public interface RpcServerTestService2 {

    void test();
}
