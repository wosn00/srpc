package com.hex.netty.protocol;

import com.hex.netty.constant.CommandType;


/**
 * @author: hs
 * <p>
 * 状态码参照HTTP
 */
public class RpcResponse extends Command<String> {

    public static final Integer SUCCESS_CODE = 200;

    public static final Integer SERVER_ERROR_CODE = 500;

    public static final Integer CLIENT_ERROR_CODE = 400;

    public static final Integer REQUEST_TIMEOUT = 408;

    public static final Integer REQUEST_DUPLICATE = 201;

    public RpcResponse() {
    }

    public RpcResponse(Long seq, String cmd, Integer code, String body) {
        super(seq, cmd, CommandType.RESPONSE_COMMAND.getValue(), code, System.currentTimeMillis(), body);
    }

    /**
     * 客户端错误响应
     */
    public static RpcResponse clientError(Long requestSeq) {
        return new RpcResponse(requestSeq, null, CLIENT_ERROR_CODE, null);
    }

    /**
     * 服务端错误响应
     */
    public static RpcResponse serverError(Long requestSeq) {
        return new RpcResponse(requestSeq, null, SERVER_ERROR_CODE, null);
    }

    /**
     * 请求超时响应
     */
    public static RpcResponse requestTimeout(Long requestSeq) {
        return new RpcResponse(requestSeq, null, REQUEST_TIMEOUT, null);
    }

    /**
     * 重复请求
     */
    public static RpcResponse duplicateRequest(Long requestSeq) {
        return new RpcResponse(requestSeq, null, REQUEST_DUPLICATE, null);
    }

    /**
     * 失败响应
     */
    public static RpcResponse failedResponse(Long requestSeq, Integer code) {
        return new RpcResponse(requestSeq, null, REQUEST_DUPLICATE, null);
    }

    /**
     * 成功响应
     *
     * @param requestSeq   请求的seq
     * @param responseBody 响应内容
     * @return RpcResponse
     */
    public static RpcResponse success(Long requestSeq, String responseBody) {
        return new RpcResponse(requestSeq, null, SUCCESS_CODE, responseBody);
    }


}
