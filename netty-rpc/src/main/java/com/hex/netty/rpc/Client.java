package com.hex.netty.rpc;

import com.hex.netty.connection.Connection;
import com.hex.netty.invoke.RpcCallback;
import com.hex.netty.protocol.RpcResponse;

import java.net.InetSocketAddress;
import java.util.List;


/**
 * @author hs
 */
public interface Client {

    /**
     * 启动客户端
     */
    Client start();

    /**
     * 停止客户端
     */
    void stop();

    /**
     * 连接集群（支持高可用，负载均衡）
     */
    Client contact(List<InetSocketAddress> cluster);

    /**
     * 连接单机
     */
    Client contact(InetSocketAddress node);

    /**
     * 根据节点发送心跳，探测节点是否能访问
     */
    boolean sendHeartBeat(InetSocketAddress node);

    /**
     * 指定连接发送心跳，探测节点是否能访问
     */
    boolean sendHeartBeat(Connection connection);

    /**
     * 根据host port发起连接
     */
    Connection connect(String host, int port);

    /**
     * 同步调用，返回整个响应内容，使用默认集群
     */
    RpcResponse invoke(String cmd, Object body);

    /**
     * 同步调用，返回整个响应内容，指定集群
     */
    RpcResponse invoke(String cmd, Object body, List<InetSocketAddress> cluster);

    /**
     * 同步调用, 并将成功响应的body自动转换为T类型
     */
    <T> T invoke(String cmd, Object body, Class<T> resultType);

    /**
     * 同步调用, 并将成功响应的body自动转换为T类型，指定集群
     */
    <T> T invoke(String cmd, Object body, Class<T> resultType, List<InetSocketAddress> cluster);

    /**
     * 异步调用
     */
    void invokeAsync(String cmd, Object body);

    /**
     * 异步调用，指定集群
     */
    void invokeAsync(String cmd, Object body, List<InetSocketAddress> cluster);

    /**
     * 异步调用，带响应回调方法
     */
    void invokeAsync(String cmd, Object body, RpcCallback callback);

    /**
     * 异步调用，带响应回调方法，指定集群
     */
    void invokeAsync(String cmd, Object body, RpcCallback callback, List<InetSocketAddress> cluster);

}
