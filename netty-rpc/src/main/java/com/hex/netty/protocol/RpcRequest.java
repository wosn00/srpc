package com.hex.netty.protocol;

import com.hex.netty.constant.CommandType;

/**
 * @author: hs
 */
public class RpcRequest<T> extends Command<T> {

    public RpcRequest() {
        super();
        super.commandType = CommandType.REQUEST_COMMAND.getValue();
    }
}
