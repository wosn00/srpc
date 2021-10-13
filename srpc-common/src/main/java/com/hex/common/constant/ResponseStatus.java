package com.hex.common.constant;

/**
 * @author: hs
 * <p>
 * 响应状态码，参考HTTP
 */
public class ResponseStatus {

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
}
