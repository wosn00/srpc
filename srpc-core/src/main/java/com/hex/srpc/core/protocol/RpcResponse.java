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
     * 重复请求
     */
    public static final Integer REQUEST_DUPLICATE = 201;

    /**
     * 服务端内部错误
     */
    public static final Integer SERVER_ERROR_CODE = 500;

    /**
     * 客户端出错
     */
    public static final Integer CLIENT_ERROR_CODE = 400;

    /**
     * 响应超时
     */
    public static final Integer RESPONSE_TIMEOUT = 502;

    /**
     * 节点暂时不可用
     */
    public static final Integer SERVICE_UNAVAILABLE = 503;

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
    public static RpcResponse responseTimeout(Long requestSeq) {
        return new RpcResponse(requestSeq, null, RESPONSE_TIMEOUT, null);
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
     * 失败响应
     */
    public static RpcResponse serviceUnAvailable(Long requestSeq) {
        return new RpcResponse(requestSeq, null, SERVICE_UNAVAILABLE, null);
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

    /**
     * 是否需要进行重试
     * 响应超时或服务节点暂时不可用才需要重试
     *
     * @return 是否要重试
     */
    public boolean isRetried() {
        return RESPONSE_TIMEOUT.equals(this.getCode()) || SERVICE_UNAVAILABLE.equals(this.getCode());
    }
}
