package com.hex.netty.protocol;

import com.hex.netty.constant.CommandType;

/**
 * @author: hs
 */
public class RpcRequest extends Command<String> {

    public RpcRequest() {
        super();
        super.commandType = CommandType.REQUEST_COMMAND.getValue();
    }
}
