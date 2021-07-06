package com.hex.handler;

import com.hex.netty.cmd.IHadnler;
import com.hex.netty.protocol.RpcRequest;

/**
 * @author: hs
 */
public class TestHandler implements IHadnler {
    @Override
    public String getCmd() {
        return "/test/cmd";
    }

    @Override
    public String handler(RpcRequest rpcRequest) {
        System.out.println("收到请求");
        System.out.println(rpcRequest);
        return "这是响应内容";
    }
}
