package com.hex.srpc.core.invoke;

import com.hex.srpc.core.protocol.RpcResponse;

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
