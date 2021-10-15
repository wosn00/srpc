package com.hex.srpc.core.rpc.client;

import com.google.common.collect.Lists;
import com.hex.common.constant.ResponseStatus;
import com.hex.common.constant.RpcConstant;
import com.hex.common.exception.ConnectionException;
import com.hex.common.exception.NodeException;
import com.hex.common.exception.RegistryException;
import com.hex.common.exception.RpcException;
import com.hex.common.id.IdGenerator;
import com.hex.common.net.HostAndPort;
import com.hex.common.spi.ExtensionLoader;
import com.hex.common.thread.SRpcThreadFactory;
import com.hex.common.utils.ThreadUtil;
import com.hex.common.utils.TypeUtil;
import com.hex.registry.ServiceDiscover;
import com.hex.srpc.core.config.SRpcClientConfig;
import com.hex.srpc.core.connection.Connection;
import com.hex.srpc.core.connection.IConnection;
import com.hex.srpc.core.handler.connection.NettyClientConnManageHandler;
import com.hex.srpc.core.handler.process.ClientProcessHandler;
import com.hex.srpc.core.invoke.ResponseFuture;
import com.hex.srpc.core.invoke.ResponseMapping;
import com.hex.srpc.core.invoke.RpcCallback;
import com.hex.srpc.core.loadbalance.LoadBalancer;
import com.hex.srpc.core.loadbalance.LoadBalancerFactory;
import com.hex.srpc.core.node.INodeManager;
import com.hex.srpc.core.node.NodeManager;
import com.hex.srpc.core.protocol.Command;
import com.hex.srpc.core.protocol.RpcRequest;
import com.hex.srpc.core.protocol.RpcResponse;
import com.hex.srpc.core.rpc.AbstractRpc;
import com.hex.srpc.core.rpc.Client;
import com.hex.srpc.core.rpc.codec.RpcPacketDecoder;
import com.hex.srpc.core.rpc.codec.RpcPacketEncoder;
import com.hex.srpc.core.rpc.task.HeartBeatTask;
import com.hex.srpc.core.rpc.task.NodeHealthCheckTask;
import com.hex.srpc.core.thread.CallBackTaskThreadPool;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hex.srpc.core.connection.Connection.CONN;

/**
 * @author hs
 */
public class SRpcClient extends AbstractRpc implements Client {

    private final Bootstrap bootstrap = new Bootstrap();
    private SRpcClientConfig config;
    private EventLoopGroup eventLoopGroupSelector;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private INodeManager nodeManager;
    private AtomicBoolean isClientStart = new AtomicBoolean(false);
    private ServiceDiscover serviceDiscover;
    private ResponseMapping responseMapping;
    private ThreadPoolExecutor callBackTaskThreadPool;

    private SRpcClient() {
    }

    public Client config(SRpcClientConfig config) {
        this.config = config;
        return this;
    }

    public static SRpcClient builder() {
        return new SRpcClient();
    }

    @Override
    public Client start() {
        if (isClientStart.compareAndSet(false, true)) {
            try {
                //1.初始化配置
                initConfig();

                //2.初始化rpc客户端
                initClient();

                //3.初始化注册中心配置
                registryInit();

                //4.注册shutdownHook
                registerShutdownHook(this::stop);

            } catch (Exception e) {
                logger.error("RpcClient started failed");
                stop();
                throw e;
            }
        } else {
            logger.warn("RpcClient already started!");
        }
        return this;
    }

    /**
     * 初始化注册中心
     */
    private void registryInit() {
        if (!checkRegistryEnable()) {
            return;
        }
        try {
            ExtensionLoader<ServiceDiscover> loader = ExtensionLoader.getExtensionLoader(ServiceDiscover.class);

            String registrySchema = this.registryConfig.getRegistrySchema();

            logger.info("use the registry schema: [{}]", registrySchema);

            this.serviceDiscover = loader.getExtension(registrySchema);

            if (this.serviceDiscover == null) {
                throw new RpcException("The registry schema" + registrySchema + " implementation was not found, " +
                        "Make sure you have implemented and configured the META-INF.srpc files using SPI");
            }
            this.serviceDiscover.initRegistry(this.registryConfig.getRegistryAddress());

        } catch (Exception e) {
            throw new RegistryException("serviceDiscovery init failed", e);
        }
    }

    @Override
    public void stop() {
        if (isClientStart.compareAndSet(true, false)) {
            logger.info("RpcClient stop []");
            try {
                if (eventLoopGroupSelector != null) {
                    eventLoopGroupSelector.shutdownGracefully();
                }
                if (defaultEventExecutorGroup != null) {
                    defaultEventExecutorGroup.shutdownGracefully();
                }
                if (callBackTaskThreadPool != null) {
                    ThreadUtil.gracefulShutdown(callBackTaskThreadPool, 5);
                }
                //关闭所有服务的连接
                nodeManager.closeManager();
            } catch (Exception e) {
                logger.error("Failed to stop RpcClient!", e);
            }
            logger.info("RpcClient stop success");
        } else {
            logger.info("RpcClient already closed");
        }

    }

    @Override
    public Client configRegistry(String schema, List<String> registryAddress) {
        setConfigRegistry(schema, registryAddress, null);
        return this;
    }

    /**
     * 根据host port发起连接, 内部使用
     */
    public IConnection connect(String host, int port) {
        if (logger.isDebugEnabled()) {
            logger.debug("RpcClient connect to host:{} port:{}", host, port);
        }
        ChannelFuture future = this.bootstrap.connect(host, port);
        Connection conn = null;
        if (future.awaitUninterruptibly(TimeUnit.SECONDS.toMillis(config.getConnectionTimeout()))) {
            if (future.channel() != null && future.channel().isActive()) {
                conn = new Connection(IdGenerator.getId(), future.channel());
                future.channel().attr(CONN).set(conn);
            } else {
                logger.error("RpcClient connect fail host:{} port:{}", host, port);
                // 记录失败次数
                NodeManager.serverError(new HostAndPort(host, port));
            }
        } else {
            logger.error("RpcClient connect fail host:{} port:{}", host, port);
            // 记录失败次数
            NodeManager.serverError(new HostAndPort(host, port));
        }
        return conn;
    }

    private void initClient() {
        logger.info("RpcClient init");

        if (useEpoll()) {
            this.eventLoopGroupSelector = new EpollEventLoopGroup(IO_THREADS);
        } else {
            this.eventLoopGroupSelector = new NioEventLoopGroup(IO_THREADS);
        }
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(config.getChannelWorkerThreads());
        // 流控
        buildTrafficMonitor(defaultEventExecutorGroup,
                config.isTrafficMonitorEnable(), config.getMaxReadSpeed(), config.getMaxWriteSpeed());

        if (config.getUseTLS() != null && config.getUseTLS()) {
            try {
                buildSSLContext(true, config);
            } catch (Exception e) {
                throw new RpcException("sRpcClient initialize SSLContext fail!", e);
            }
        }

        this.bootstrap.group(this.eventLoopGroupSelector)
                .channel(useEpoll() ? EpollSocketChannel.class : NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, (int) TimeUnit.SECONDS.toMillis(config.getConnectionTimeout()))
                .option(ChannelOption.SO_SNDBUF, this.config.getSendBuf())
                .option(ChannelOption.SO_RCVBUF, this.config.getReceiveBuf())
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK,
                        new WriteBufferWaterMark(this.config.getLowWaterLevel(), this.config.getHighWaterLevel()))
                .handler(new ClientChannel());

        // 心跳保活
        Executors.newSingleThreadScheduledExecutor(SRpcThreadFactory.getDefault())
                .scheduleAtFixedRate(new HeartBeatTask(this.nodeManager, this), 3, config.getHeartBeatTimeInterval(),
                        TimeUnit.SECONDS);
        logger.info("RpcClient init success");
    }

    @Override
    public boolean sendHeartBeat(HostAndPort node) {
        IConnection connection = getConnection(Lists.newArrayList(node), null);
        return sendHeartBeat(connection);
    }

    @Override
    public boolean sendHeartBeat(IConnection connection) {
        //构造心跳包
        RpcRequest ping = buildHeartBeatPacket();
        ResponseFuture responseFuture;
        //发送心跳
        try {
            responseFuture = sendCommand(ping, connection, null, 5);
        } catch (Exception e) {
            logger.error("sync send heartBeat packet error", e);
            responseMapping.invalidate(ping.getSeq());
            return false;
        }
        //等待并获取响应
        RpcResponse pong = responseFuture.waitForResponse();
        return RpcConstant.PONG.equals(pong.getBody());
    }

    @Override
    public RpcResponse invoke(String mapping, Object[] args, HostAndPort... nodes) {
        return invoke(mapping, 0, args, nodes);
    }

    @Override
    public RpcResponse invoke(String mapping, int retryTimes, Object[] args, HostAndPort... nodes) {
        assertNodesNotNull(nodes);
        RpcResponse response;
        do {
            response = send(mapping, args, nodes, null, false);
        } while (retryTimes-- > 0 && response.isRetried());
        return response;
    }

    @Override
    public <T> T invoke(String mapping, Class<T> resultType, Object[] args, HostAndPort... nodes) {
        return invoke(mapping, resultType, 0, args, nodes);
    }

    @Override
    public <T> T invoke(String mapping, Class<T> resultType, int retryTimes, Object[] args, HostAndPort... nodes) {
        RpcResponse response = invoke(mapping, retryTimes, args, nodes);
        if (ResponseStatus.SUCCESS_CODE.equals(response.getStatus())) {
            Object body = response.getBody();
            return TypeUtil.convert(body, resultType,
                    "the resultType dose not match the response body, response body type:" + (body == null ? "Void" : body.getClass()));
        } else {
            logger.warn("The response status to this request {} is {}", response.getSeq(), response.getStatus());
            return null;
        }
    }

    @Override
    public void invokeAsync(String mapping, RpcCallback callback, Object[] args, HostAndPort... nodes) {
        assertNodesNotNull(nodes);
        send(mapping, args, nodes, callback, true);
    }

    @Override
    public RpcResponse invokeWithRegistry(String mapping, String serviceName, Object[] args) {
        return invokeWithRegistry(mapping, serviceName, 0, args);
    }

    @Override
    public RpcResponse invokeWithRegistry(String mapping, String serviceName, int retryTimes, Object[] args) {
        registryConfigCheck();
        return invoke(mapping, retryTimes, args, discoverRpcService(serviceName));
    }

    @Override
    public <T> T invokeWithRegistry(String mapping, Class<T> resultType, String serviceName, Object[] args) {
        return invokeWithRegistry(mapping, resultType, serviceName, 0, args);
    }

    @Override
    public <T> T invokeWithRegistry(String mapping, Class<T> resultType, String serviceName, int retryTimes, Object[] args) {
        registryConfigCheck();
        return invoke(mapping, resultType, retryTimes, args, discoverRpcService(serviceName));
    }

    @Override
    public void invokeAsyncWithRegistry(String mapping, RpcCallback callback, String serviceName, Object[] args) {
        registryConfigCheck();
        invokeAsync(mapping, callback, args, discoverRpcService(serviceName));
    }

    private RpcRequest buildRequest(String mapping, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setSeq(IdGenerator.getId());
        if (StringUtils.isBlank(mapping)) {
            throw new IllegalArgumentException("mapping can not be null");
        }
        request.setMapping(mapping);
        request.setTimestamp(System.currentTimeMillis());
        request.setArgs(args);
        return request;
    }

    private RpcResponse send(String mapping, Object[] args, HostAndPort[] nodes, RpcCallback callback, boolean sendAsync) {
        // 构造请求
        RpcRequest request = buildRequest(mapping, args);
        RpcResponse response = null;
        ResponseFuture responseFuture;
        try {
            responseFuture = sendCommand(request, Arrays.asList(nodes), callback, config.getRequestTimeout());

        } catch (ConnectionException | NodeException e) {
            failed(request, e);
            return RpcResponse.serviceUnAvailable(request.getSeq());

        } catch (Exception e) {
            failed(request, e);
            return RpcResponse.clientError(request.getSeq());
        }
        if (!sendAsync) {
            // 等待并获取响应
            response = responseFuture.waitForResponse();
        }
        return response;
    }

    private ResponseFuture sendCommand(RpcRequest request, List<HostAndPort> nodes,
                                       RpcCallback callback, Integer requestTimeout) {
        // 获取连接
        IConnection connection = getConnection(nodes, request);
        // 发送请求
        return sendCommand(request, connection, callback, requestTimeout);
    }

    private ResponseFuture sendCommand(Command command, IConnection connection,
                                       RpcCallback callback, Integer requestTimeout) {
        ResponseFuture responseFuture =
                new ResponseFuture(command.getSeq(), requestTimeout, connection.getRemoteAddress(), callback);
        responseMapping.putResponseFuture(command.getSeq(), responseFuture);
        connection.send(command);
        return responseFuture;
    }

    private IConnection getConnection(List<HostAndPort> nodes, RpcRequest request) {
        IConnection connection = nodeManager.chooseConnection(nodes, request);
        if (connection == null) {
            throw new ConnectionException("No connection available");
        }
        return connection;
    }

    private RpcRequest buildHeartBeatPacket() {
        RpcRequest ping = new RpcRequest();
        ping.setSeq(IdGenerator.getId());
        ping.setHeartBeat(true);
        ping.setArgs(new Object[]{RpcConstant.PING});
        return ping;
    }

    private void initConfig() {
        assertNotNull(config, "clientConfig can't be null, Please confirm that you have configured");

        ExtensionLoader<CallBackTaskThreadPool> loader = ExtensionLoader.getExtensionLoader(CallBackTaskThreadPool.class);
        CallBackTaskThreadPool callBackTaskThreadPoolImpl = loader.getExtension(RpcConstant.SPI_CUSTOM_IMPL);
        if (callBackTaskThreadPoolImpl != null) {
            //若自定义了回调任务线程池则使用自定义的线程池
            callBackTaskThreadPool = callBackTaskThreadPoolImpl.getCallBackTaskThreadPool();
            logger.info("Use the custom callBackTaskThreadPool [{}]", callBackTaskThreadPool.getClass().getCanonicalName());
        } else {
            if (config.getCallBackTaskThreads() != null && config.getCallBackTaskThreads() > 0) {
                Integer coreThreads = config.getCallBackTaskThreads();
                Integer queueSize = config.getCallBackTaskQueueSize();
                callBackTaskThreadPool = ThreadUtil.getFixThreadPoolExecutor(coreThreads, queueSize,
                        new ThreadPoolExecutor.AbortPolicy(), "sRpc-client-business");
            }
        }
        if (callBackTaskThreadPool != null) {
            ResponseFuture.setTaskExecutor(callBackTaskThreadPool);
        }
        LoadBalancer loadBalancer = LoadBalancerFactory.getLoadBalance(config.getLoadBalanceRule());
        nodeManager = new NodeManager(true, this, config.getConnectionSizePerNode(), loadBalancer);
        if (config.isExcludeUnAvailableNodesEnable()) {
            nodeManager.setExcludeUnAvailableNodesEnable(true);
            NodeManager.setNodeErrorTimes(config.getNodeErrorTimes());
            //节点健康检查
            Executors.newSingleThreadScheduledExecutor(SRpcThreadFactory.getDefault())
                    .scheduleAtFixedRate(new NodeHealthCheckTask(nodeManager), 0, config.getNodeHealthCheckTimeInterval(), TimeUnit.SECONDS);
        }
        responseMapping = new ResponseMapping(config.getRequestTimeout());
    }

    private void assertNodesNotNull(HostAndPort[] nodes) {
        if (nodes == null || nodes.length == 0) {
            throw new IllegalArgumentException("node can not be null");
        }
    }

    private HostAndPort[] discoverRpcService(String serviceName) {
        try {
            return serviceDiscover.discoverRpcServiceAddress(serviceName).toArray(new HostAndPort[]{});
        } catch (Exception e) {
            throw new RegistryException("discover rpc service address failed", e);
        }
    }

    private void registryConfigCheck() {
        if (this.registryConfig == null || !this.registryConfig.isEnableRegistry()) {
            throw new RpcException("Registry not configured");
        }
    }

    private void failed(Command command, Exception e) {
        logger.error("command send error", e);
        responseMapping.invalidate(command.getSeq());
    }

    /**
     * Rpc客户端channel
     */
    class ClientChannel extends ChannelInitializer<SocketChannel> {

        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();
            // 流控
            if (null != trafficShapingHandler) {
                pipeline.addLast(defaultEventExecutorGroup, "trafficShapingHandler", trafficShapingHandler);
            }
            //tls加密
            if (null != sslContext) {
                pipeline.addLast(defaultEventExecutorGroup, "sslHandler", sslContext.newHandler(ch.alloc()));
            }
            // 添加压缩编解码
            pipeline.addLast(
                    defaultEventExecutorGroup,
                    new RpcPacketDecoder(),
                    new RpcPacketEncoder(config.getCompressType(), config.getSerializeType()),

                    new IdleStateHandler(config.getConnectionIdleTime(), config.getConnectionIdleTime(), 0),
                    new NettyClientConnManageHandler(nodeManager),
                    new ClientProcessHandler(nodeManager, responseMapping, config));
        }
    }
}
