package com.hex.example.service;

import com.hex.common.annotation.SRpcClient;
import com.hex.example.entity.TestRequest;
import com.hex.example.entity.TestResponse;

/**
 * @author: hs
 * <p>
 * 指定节点列表
 */
@SRpcClient(nodes = "${srpc.testService}", retryTimes = 2)
public interface RpcServerTestService {

    String handler(String body);

    TestResponse handler2(TestRequest request);

    void handler3();
}
