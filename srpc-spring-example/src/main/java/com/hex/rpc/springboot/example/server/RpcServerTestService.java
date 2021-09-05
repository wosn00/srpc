package com.hex.rpc.springboot.example.server;

import com.hex.common.annotation.RouteBody;
import com.hex.common.annotation.RouteMapping;
import com.hex.rpc.sping.annotation.RpcClient;
import com.hex.rpc.springboot.example.entity.TestRequest;
import com.hex.rpc.springboot.example.entity.TestResponse;

/**
 * @author: hs
 */
@RpcClient(servers = {"127.0.0.1:8008"})
public interface RpcServerTestService {

    @RouteMapping("test1")
    String handler(@RouteBody String body);

    @RouteMapping("test2")
    TestResponse handler2(@RouteBody TestRequest request);
}
