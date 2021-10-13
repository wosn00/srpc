package com.hex.srpc.core.rpc;

import com.hex.common.annotation.Nullable;
import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.connection.IConnection;
import com.hex.srpc.core.invoke.RpcCallback;
import com.hex.srpc.core.protocol.RpcResponse;

import java.util.List;

/**
 * @author hs
 * <p>
 * SRpc客户端
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
     * @return Client
     */
    Client configRegistry(String schema, List<String> registryAddress);

    /**
     * 根据节点发送心跳，探测节点是否能访问
     *
     * @param nodes 节点地址端口
     * @return true:节点间通信正常  false:节点间通信失败
     */
    boolean sendHeartBeat(HostAndPort nodes);

    /**
     * 指定连接发送心跳，探测节点是否能访问
     *
     * @param connection 与节点的连接
     * @return true:连接通信正常  false:连接通信失败
     */
    boolean sendHeartBeat(IConnection connection);

    /**
     * 同步调用，返回整个响应内容，指定rpc服务端节点
     *
     * @param mapping 服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args    请求实体列表
     * @param nodes   指定服务端节点[数量为1时不支持节点容错]
     * @return 响应内容
     */
    RpcResponse invoke(String mapping, Object[] args, HostAndPort... nodes);

    /**
     * 同步调用，返回整个响应内容，指定rpc服务节点，带超时重试机制
     *
     * @param mapping    服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args       请求实体列表
     * @param nodes      指定多个服务端节点
     * @param retryTimes 失败重试次数
     * @return 响应内容
     */
    RpcResponse invoke(String mapping, int retryTimes, Object[] args, HostAndPort... nodes);

    /**
     * 同步调用, 并将成功响应的args自动转换为T类型
     *
     * @param mapping    服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args       请求实体列表
     * @param resultType 响应实体类型
     * @param nodes      指定服务端节点[数量为1时不支持节点容错]
     * @return 转换后的args响应内容实体
     */
    <T> T invoke(String mapping, Class<T> resultType, Object[] args, HostAndPort... nodes);

    /**
     * 同步调用, 并将成功响应的args自动转换为T类型，指定节点
     *
     * @param mapping    服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args       请求实体列表
     * @param resultType 响应实体类型
     * @param nodes      指定多个服务端节点
     * @param retryTimes 失败重试次数
     * @return 转换后的args响应内容实体
     */
    <T> T invoke(String mapping, Class<T> resultType, int retryTimes, Object[] args, HostAndPort... nodes);

    /**
     * 异步调用，带响应回调方法
     *
     * @param mapping  服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args     请求实体列表
     * @param callback 响应回调任务
     * @param nodes    指定服务端节点[数量为1时不支持节点容错]
     */
    void invokeAsync(String mapping, @Nullable RpcCallback callback, Object[] args, HostAndPort... nodes);

    /**
     * 同步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     *
     * @param mapping     服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args        请求实体列表
     * @param serviceName 服务名称[注册到注册中心的服务名称]
     * @return 响应内容
     */
    RpcResponse invokeWithRegistry(String mapping, String serviceName, Object[] args);

    /**
     * 同步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     *
     * @param mapping     服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args        请求实体列表
     * @param serviceName 服务名称[注册到注册中心的服务名称]
     * @param retryTimes  失败重试次数
     * @return 响应内容
     */
    RpcResponse invokeWithRegistry(String mapping, String serviceName, int retryTimes, Object[] args);

    /**
     * 同步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     *
     * @param mapping     服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args        请求实体列表
     * @param resultType  响应实体类型
     * @param serviceName 服务名称[注册到注册中心的服务名称]
     * @return 响应内容实体
     */
    <T> T invokeWithRegistry(String mapping, Class<T> resultType, String serviceName, Object[] args);

    /**
     * 同步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     *
     * @param mapping     服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args        请求实体列表
     * @param resultType  响应实体类型
     * @param serviceName 服务名称[注册到注册中心的服务名称]
     * @param retryTimes  失败重试次数
     * @return 响应内容实体
     */
    <T> T invokeWithRegistry(String mapping, Class<T> resultType, String serviceName, int retryTimes, Object[] args);

    /**
     * 异步调用, 使用注册中心获取服务地址[需配置注册中心地址]
     *
     * @param mapping     服务端对应处理器的mapping标识，服务端@mapping注解的值
     * @param args        请求实体列表
     * @param callback    响应回调任务
     * @param serviceName 服务名称[注册到注册中心的服务名称]
     */
    void invokeAsyncWithRegistry(String mapping, @Nullable RpcCallback callback, String serviceName, Object[] args);

}
