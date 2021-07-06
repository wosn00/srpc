package com.hex.netty.invoke;

import com.hex.netty.protocol.RpcResponse;

/**
 * @author: hs
 */
@FunctionalInterface
public interface RpcCallback {

    /**
     * 请求回调
     */
    void callback(RpcResponse rpcResponse);

}
