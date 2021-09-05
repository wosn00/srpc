package com.hex.srpc.core.protocol;

import com.hex.common.constant.CommandType;

/**
 * @author: hs
 */
public class RpcRequest extends Command<String> {

    public RpcRequest() {
        super();
        super.commandType = CommandType.REQUEST_COMMAND.getValue();
    }
}
