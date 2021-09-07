package com.hex.srpc.core.rpc;

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
     * 连接多服务节点（支持高可用，负载均衡）
     */
    Client contactNodes(List<HostAndPort> nodes);

    /**
     * 连接单服务节点
     */
    Client contactNode(HostAndPort node);

    /**
     * 设置注册中心地址
     *
     * @param schema          注册中心协议[默认zookeeper]
     * @param registryAddress 注册中心地址
     * @return
     */
    Client registryAddress(String schema, List<String> registryAddress);

    /**
     * 根据节点发送心跳，探测节点是否能访问
     */
    boolean sendHeartBeat(HostAndPort node);

    /**
     * 指定连接发送心跳，探测节点是否能访问
     */
    boolean sendHeartBeat(IConnection connection);

    /**
     * 同步调用，返回整个响应内容，使用默认集群
     */
    RpcResponse invoke(String cmd, Object body, HostAndPort... nodes);

    /**
     * 同步调用，返回整个响应内容，指定rpc服务节点
     */
    RpcResponse invoke(String cmd, Object body, List<HostAndPort> nodes);

    /**
     * 同步调用, 并将成功响应的body自动转换为T类型
     */
    <T> T invoke(String cmd, Object body, Class<T> resultType, HostAndPort... nodes);

    /**
     * 同步调用, 并将成功响应的body自动转换为T类型，指定节点
     */
    <T> T invoke(String cmd, Object body, Class<T> resultType, List<HostAndPort> nodes);

    /**
     * 异步调用
     */
    void invokeAsync(String cmd, Object body, HostAndPort... nodes);

    /**
     * 异步调用，指定rpc服务节点
     */
    void invokeAsync(String cmd, Object body, List<HostAndPort> nodes);

    /**
     * 异步调用，带响应回调方法
     */
    void invokeAsync(String cmd, Object body, RpcCallback callback, HostAndPort... nodes);

    /**
     * 异步调用，带响应回调方法，指定rpc服务节点
     */
    void invokeAsync(String cmd, Object body, RpcCallback callback, List<HostAndPort> nodes);

    /**
     * 同步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     */
    RpcResponse invokeWithRegistry(String cmd, Object body, String serviceName);

    /**
     * 同步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     */
    <T> T invokeWithRegistry(String cmd, Object body, Class<T> resultType, String serviceName);

    /**
     * 异步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     */
    void invokeAsyncWithRegistry(String cmd, Object body, String serviceName);

    /**
     * 异步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     */
    void invokeAsyncWithRegistry(String cmd, Object body, RpcCallback callback, String serviceName);

}
