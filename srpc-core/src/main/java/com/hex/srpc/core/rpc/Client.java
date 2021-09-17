package com.hex.srpc.core.rpc;

import com.hex.common.annotation.Nullable;
import com.hex.srpc.core.connection.IConnection;
import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.invoke.RpcCallback;
import com.hex.srpc.core.protocol.RpcResponse;

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
     * 设置注册中心地址
     *
     * @param schema          注册中心协议[默认zookeeper]
     * @param registryAddress 注册中心地址
     * @return
     */
    Client configRegistry(String schema, List<String> registryAddress);

    /**
     * 根据节点发送心跳，探测节点是否能访问
     */
    boolean sendHeartBeat(HostAndPort node);

    /**
     * 指定连接发送心跳，探测节点是否能访问
     */
    boolean sendHeartBeat(IConnection connection);

    /**
     * 同步调用，返回整个响应内容，指定rpc服务端节点
     */
    RpcResponse invoke(String cmd, Object body, HostAndPort node);

    /**
     * 同步调用，返回整个响应内容，指定rpc服务端节点
     */
    RpcResponse invoke(String cmd, Object body, List<HostAndPort> nodes);

    /**
     * 同步调用，返回整个响应内容，指定rpc服务节点，带超时重试机制
     */
    RpcResponse invoke(String cmd, Object body, List<HostAndPort> nodes, int retryTimes);

    /**
     * 同步调用, 并将成功响应的body自动转换为T类型
     */
    <T> T invoke(String cmd, Object body, Class<T> resultType, HostAndPort node);

    /**
     * 同步调用, 并将成功响应的body自动转换为T类型，指定节点
     */
    <T> T invoke(String cmd, Object body, Class<T> resultType, List<HostAndPort> nodes);

    /**
     * 同步调用, 并将成功响应的body自动转换为T类型，指定节点
     */
    <T> T invoke(String cmd, Object body, Class<T> resultType, List<HostAndPort> nodes, int retryTimes);

    /**
     * 异步调用，带响应回调方法
     */
    void invokeAsync(String cmd, Object body, @Nullable RpcCallback callback, HostAndPort node);

    /**
     * 异步调用，带响应回调方法，指定rpc服务端节点
     */
    void invokeAsync(String cmd, Object body, @Nullable RpcCallback callback, List<HostAndPort> nodes);

    /**
     * 同步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     */
    RpcResponse invokeWithRegistry(String cmd, Object body, String serviceName);

    /**
     * 同步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     */
    RpcResponse invokeWithRegistry(String cmd, Object body, String serviceName, int retryTimes);

    /**
     * 同步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     */
    <T> T invokeWithRegistry(String cmd, Object body, Class<T> resultType, String serviceName);

    /**
     * 同步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     */
    <T> T invokeWithRegistry(String cmd, Object body, Class<T> resultType, String serviceName, int retryTimes);

    /**
     * 异步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     */
    void invokeAsyncWithRegistry(String cmd, Object body, @Nullable RpcCallback callback, String serviceName);

}
