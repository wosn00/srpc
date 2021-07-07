package com.hex.netty.cmd;

import com.hex.netty.protocol.RpcRequest;

/**
 * @author: hs
 */
public interface IHandler {

    String getCmd();

    String handler(RpcRequest rpcRequest);

}
