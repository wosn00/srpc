package com.hex.handler;

import com.hex.netty.annotation.RouteBody;
import com.hex.netty.annotation.RouteMapping;
import com.hex.netty.annotation.RpcRoute;

/**
 * @author: hs
 */
@RpcRoute
public class TestRouter {

    @RouteMapping("/test/cmd")
    public String handler(@RouteBody String body) {
        System.out.println("收到请求内容：" + body);
        return "这是响应内容";
    }
}
