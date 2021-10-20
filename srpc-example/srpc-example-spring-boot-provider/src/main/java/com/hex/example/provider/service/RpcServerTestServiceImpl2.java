package com.hex.example.provider.service;

import com.hex.common.annotation.SRpcRoute;
import com.hex.example.service.RpcServerTestService2;

/**
 * @author: hs
 * <p>
 * SrpcRoute服务2
 */
@SRpcRoute
public class RpcServerTestServiceImpl2 implements RpcServerTestService2 {

    @Override
    public void test() {
        System.out.println("====test3====");
    }
}
