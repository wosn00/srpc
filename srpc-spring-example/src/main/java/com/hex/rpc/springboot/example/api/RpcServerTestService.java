package com.hex.rpc.springboot.example.api;

import com.hex.common.annotation.RouteBody;
import com.hex.common.annotation.RouteMapping;
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

    @RouteMapping("test1")
    String handler(@RouteBody String body);

    @RouteMapping("test2")
    TestResponse handler2(@RouteBody TestRequest request);
}
