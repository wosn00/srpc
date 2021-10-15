package com.hex.example.provider.service;

import com.hex.common.annotation.Mapping;
import com.hex.common.annotation.SRpcRoute;

/**
 * @author: hs
 * <p>
 * SrpcRoute服务2
 */
@SRpcRoute
public class RpcServerTestServiceImpl2 {

    @Mapping("test3")
    public void test3() {
        System.out.println("====test3====");
    }
}
