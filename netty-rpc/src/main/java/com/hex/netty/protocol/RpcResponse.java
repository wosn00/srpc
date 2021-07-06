package com.hex.netty.protocol;

import com.hex.netty.constant.CommandType;

import java.util.UUID;

/**
 * @author: hs
 */
public class RpcResponse extends Command<String> {

    public static final Integer SUCCESS_CODE = 200;

    public static final Integer SERVER_ERROR_CODE = 500;

    public static final Integer CLIENT_ERROR_CODE = 400;

    public static final Integer REQUEST_TIMEOUT = 408;

    public RpcResponse() {
    }

    public RpcResponse(String seq, String cmd, Integer code, String body) {
        super(seq, cmd, CommandType.RESPONSE_COMMAND.getValue(), code, System.currentTimeMillis(), body);
    }

    /**
     * 客户端错误响应
     */
    public static RpcResponse clientError() {
        return new RpcResponse(UUID.randomUUID().toString(), null, CLIENT_ERROR_CODE, null);
    }

    /**
     * 服务端错误响应
     */
    public static RpcResponse serverError() {
        return new RpcResponse(UUID.randomUUID().toString(), null, SERVER_ERROR_CODE, null);
    }

    /**
     * 请求超时响应
     */
    public static RpcResponse requestTimeout() {
        return new RpcResponse(UUID.randomUUID().toString(), null, REQUEST_TIMEOUT, null);
    }

    /**
     * 成功响应
     *
     * @param requestSeq   请求的seq
     * @param responseBody 响应内容
     * @return RpcResponse
     */
    public static RpcResponse success(String requestSeq, String responseBody) {
        return new RpcResponse(requestSeq, null, SUCCESS_CODE, responseBody);
    }


}
