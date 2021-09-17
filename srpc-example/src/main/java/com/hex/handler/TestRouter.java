package com.hex.handler;

import com.hex.entity.TestRequest;
import com.hex.entity.TestResponse;
import com.hex.common.annotation.RouteBody;
import com.hex.common.annotation.RouteMapping;
import com.hex.common.annotation.SRpcRoute;

/**
 * @author: hs
 */
@SRpcRoute
public class TestRouter {

    @RouteMapping("test1")
    public String handler(@RouteBody String body) {

        System.out.println("test1收到请求内容：" + body);

        return "这是test1响应内容";
    }

    @RouteMapping("test2")
    public TestResponse handler2(@RouteBody TestRequest request) {

        System.out.println("test2收到请求内容:" + request);

        return new TestResponse().setResponse("test2响应结果");
    }
}
