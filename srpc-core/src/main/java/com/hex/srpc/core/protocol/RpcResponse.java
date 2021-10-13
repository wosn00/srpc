package com.hex.srpc.core.protocol;

import com.hex.common.constant.ResponseStatus;


/**
 * @author: hs
 */
public class RpcResponse extends Command {

    private static final long serialVersionUID = 5165433995594793128L;

    /**
     * 响应状态码, 参考HTTP状态码
     */
    private Integer status;

    /**
     * 响应内容
     */
    private Object body;

    public RpcResponse() {
    }

    public RpcResponse(Long seq, String header, String mapping, Long timestamp, Integer status, Object body) {
        super(seq, header, mapping, false, timestamp);
        this.status = status;
        this.body = body;
    }

    public RpcResponse(Long seq, Integer status) {
        this(seq, null, null, System.currentTimeMillis(), status, null);
    }

    public Integer getStatus() {
        return status;
    }

    public RpcResponse setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public Object getBody() {
        return body;
    }

    public RpcResponse setBody(Object body) {
        this.body = body;
        return this;
    }

    /**
     * 客户端错误响应
     */
    public static RpcResponse clientError(Long requestSeq) {
        return new RpcResponse(requestSeq, ResponseStatus.CLIENT_ERROR_CODE);
    }

    /**
     * 服务端错误响应
     */
    public static RpcResponse serverError(Long requestSeq) {
        return new RpcResponse(requestSeq, ResponseStatus.SERVER_ERROR_CODE);
    }

    /**
     * 请求超时响应
     */
    public static RpcResponse responseTimeout(Long requestSeq) {
        return new RpcResponse(requestSeq, ResponseStatus.RESPONSE_TIMEOUT);
    }

    /**
     * 重复请求
     */
    public static RpcResponse duplicateRequest(Long requestSeq) {
        return new RpcResponse(requestSeq, ResponseStatus.REQUEST_DUPLICATE);
    }

    /**
     * 失败响应
     */
    public static RpcResponse serviceUnAvailable(Long requestSeq) {
        return new RpcResponse(requestSeq, ResponseStatus.SERVICE_UNAVAILABLE);
    }

    /**
     * 成功响应
     *
     * @param requestSeq   请求的seq
     * @param responseBody 响应内容
     * @return RpcResponse
     */
    public static RpcResponse success(Long requestSeq, String mapping, Object responseBody) {
        return new RpcResponse(requestSeq, null, mapping, System.currentTimeMillis(),
                ResponseStatus.SUCCESS_CODE, responseBody);
    }

    /**
     * 是否需要进行重试
     * 响应超时或服务节点暂时不可用才需要重试
     *
     * @return 是否要重试
     */
    public boolean isRetried() {
        return ResponseStatus.RESPONSE_TIMEOUT.equals(this.getStatus()) || ResponseStatus.SERVICE_UNAVAILABLE.equals(this.getStatus());
    }
}
