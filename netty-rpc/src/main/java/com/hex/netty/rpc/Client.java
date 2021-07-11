package com.hex.netty.rpc;

import com.hex.netty.connection.Connection;
import com.hex.netty.invoke.RpcCallback;
import com.hex.netty.protocol.RpcResponse;


/**
 * @author hs
 */
public interface Client {

    /**
     * 启动客户端
     */
    void start();

    /**
     * 停止客户端
     */
    void stop();

    /**
     * 与服务器建立单连接
     */
    Connection connect(String host, int port);

    /**
     * 与服务器建立连接, 可设置初始连接数
     */
    void connect(String host, int port, int connectionNum);

    /**
     * 同步调用，返回整个响应内容，不自动转换响应内容
     */
    RpcResponse invoke(String cmd, Object body);

    /**
     * 同步调用, 并将成功响应的body自动转换为T类型
     */
    <T> T invoke(String cmd, Object body, Class<T> resultType);

    /**
     * 异步调用
     */
    void invokeAsync(String cmd, Object body);

    /**
     * 异步调用，带响应回调方法
     */
    void invokeAsync(String cmd, Object body, RpcCallback callback);

}
