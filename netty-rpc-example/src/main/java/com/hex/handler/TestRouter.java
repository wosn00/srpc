package com.hex.handler;

import com.hex.netty.annotation.RouteBody;
import com.hex.netty.annotation.RouteMapping;
import com.hex.netty.annotation.RpcRoute;

/**
 * @author: hs
 */
@RpcRoute
public class TestRouter {

    @RouteMapping("/myRouter/test1")
    public String handler(@RouteBody String body) {
        System.out.println("test1收到请求内容：" + body);
        return "这是test1响应内容";
    }

    @RouteMapping("/myRouter/test2")
    public String handler2(@RouteBody String body) {
        System.out.println("test2收到请求内容：" + body);
        return "这是test2响应内容";
    }
}
