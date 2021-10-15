package com.hex.example.service;

import com.hex.common.annotation.Mapping;
import com.hex.rpc.sping.annotation.SRpcClient;
import com.hex.rpc.springboot.example.entity.TestRequest;
import com.hex.rpc.springboot.example.entity.TestResponse;

/**
 * @author: hs
 * <p>
 * 指定节点列表
 */
@SRpcClient(nodes = {"127.0.0.1:8008"}, retryTimes = 2)
public interface RpcServerTestService {

    @Mapping("test1")
    String handler(String body);

    @Mapping("test2")
    TestResponse handler2(TestRequest request);

    @Mapping("test3")
    void handler3();
}
