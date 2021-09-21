package com.hex.rpc.springboot.example.provider;

import com.hex.common.annotation.RouteMapping;
import com.hex.common.annotation.SRpcRoute;

/**
 * @author: hs
 * <p>
 * SprcRoute服务2
 */
@SRpcRoute
public class RpcServerTestServiceImpl2 {

    @RouteMapping("test3")
    public void test3() {
        System.out.println("====test3====");
    }
}
