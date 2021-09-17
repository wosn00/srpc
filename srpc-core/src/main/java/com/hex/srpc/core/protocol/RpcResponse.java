package com.hex.srpc.core.protocol;

import com.hex.common.constant.CommandType;


/**
 * @author: hs
 * <p>
 * 状态码参照HTTP
 */
public class RpcResponse extends Command<String> {

    /**
     * 响应成功
     */
    public static final Integer SUCCESS_CODE = 200;

    /**
     * 服务端内部错误
     */
    public static final Integer SERVER_ERROR_CODE = 500;

    /**
     * 客户端出错
     */
    public static final Integer CLIENT_ERROR_CODE = 400;

    /**
     * 请求超时
     */
    public static final Integer REQUEST_TIMEOUT = 408;

    /**
     * 重复请求
     */
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
    public static Command<Object> success(Long requestSeq, String cmd, Object responseBody) {
        return new Command<>(requestSeq, cmd, CommandType.RESPONSE_COMMAND.getValue(), SUCCESS_CODE, System.currentTimeMillis(), responseBody);
    }

    public boolean isTimeout() {
        return REQUEST_TIMEOUT.equals(this.getCode());
    }
}
