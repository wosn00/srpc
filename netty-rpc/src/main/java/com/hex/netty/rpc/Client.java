package com.hex.netty.rpc;

import com.hex.netty.connection.Connection;
import com.hex.netty.invoke.RpcCallback;
import com.hex.netty.protocol.RpcRequest;
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
     * 与服务器建立连接
     */
    Connection connect(String host, int port);

    /**
     * 与服务器建立连接, 初始连接数
     */
    void connect(String host, int port, int connectionNum);

    /**
     * 同步调用
     */
    RpcResponse invoke(RpcRequest rpcRequest);

    /**
     * 异步调用
     */
    void invokeAsync(RpcRequest rpcRequest);

    /**
     * 异步调用，带响应回调方法
     */
    void invokeAsync(RpcRequest rpcRequest, RpcCallback callback);

}
