package com.hex.srpc.core.rpc.client;

import com.google.common.collect.Lists;
import com.hex.common.constant.CommandType;
import com.hex.common.constant.RpcConstant;
import com.hex.common.exception.RegistryException;
import com.hex.common.exception.RpcException;
import com.hex.common.id.IdGenerator;
import com.hex.common.net.HostAndPort;
import com.hex.common.spi.ExtensionLoader;
import com.hex.common.thread.SRpcThreadFactory;
import com.hex.common.utils.SerializerUtil;
import com.hex.discovery.ServiceDiscover;
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
import com.hex.srpc.core.protocol.pb.proto.Rpc;
import com.hex.srpc.core.rpc.AbstractRpc;
import com.hex.srpc.core.rpc.Client;
import com.hex.srpc.core.rpc.compress.JdkZlibExtendDecoder;
import com.hex.srpc.core.rpc.compress.JdkZlibExtendEncoder;
import com.hex.srpc.core.rpc.task.HeartBeatTask;
import com.hex.srpc.core.rpc.task.NodeHealthCheckTask;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.Executors;
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
    private LoadBalancer loadBalancer;
    private ServiceDiscover serviceDiscover;
    private ResponseMapping responseMapping;

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
                //初始化服务节点管理器
                initConfig();

                initClient();

                registryInit();

                registerShutdownHook(this::stop);

            } catch (Exception e) {
                logger.error("RpcClient started failed");
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

            this.serviceDiscover.initRegistry(this.registryConfig.getRegistryAddress());

        } catch (Exception e) {
            throw new RegistryException("serviceDiscovery init failed", e);
        }
    }

    @Override
    public void stop() {
        if (!isClientStart.get()) {
            logger.warn("RpcClient does not start");
            return;
        }
        logger.info("RpcClient stop ...");
        try {
            if (eventLoopGroupSelector != null) {
                eventLoopGroupSelector.shutdownGracefully();
            }
            if (defaultEventExecutorGroup != null) {
                defaultEventExecutorGroup.shutdownGracefully();
            }
            //关闭所有服务的连接
            nodeManager.closeManager();
        } catch (Exception e) {
            logger.error("Failed to stop RpcClient!", e);
        }
        logger.info("RpcClient stop success");
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
        logger.info("RpcClient init ...");

        if (useEpoll()) {
            this.eventLoopGroupSelector = new EpollEventLoopGroup(config.getSelectorThreads());
        } else {
            this.eventLoopGroupSelector = new NioEventLoopGroup(config.getSelectorThreads());
        }
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(config.getWorkerThreads());
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
        Command<String> heartBeatPacket = buildHeartBeatPacket();
        ResponseFuture responseFuture;
        //发送心跳
        try {
            responseFuture = sendCommand(heartBeatPacket, connection, null, 5);
        } catch (Exception e) {
            logger.error("sync send heartBeat packet error", e);
            responseMapping.invalidate(heartBeatPacket.getSeq());
            return false;
        }
        //等待并获取响应
        Command<String> command = responseFuture.waitForResponse();
        return RpcConstant.PONG.equals(command.getBody());
    }

    @Override
    public RpcResponse invoke(String cmd, Object body, HostAndPort node) {
        return invoke(cmd, body, Lists.newArrayList(node));
    }

    @Override
    public RpcResponse invoke(String cmd, Object body, List<HostAndPort> nodes) {
        return invoke(cmd, body, nodes, 0);
    }

    @Override
    public RpcResponse invoke(String cmd, Object body, List<HostAndPort> nodes, int retryTimes) {
        assertNodesNotNull(nodes);
        RpcResponse response;
        do {
            ResponseFuture responseFuture;
            // 构造请求
            RpcRequest<?> request = buildRequest(cmd, body);
            try {
                // 发送请求
                responseFuture = sendCommand(request, nodes, null, config.getRequestTimeout());
            } catch (Exception e) {
                logger.error("sync send request error", e);
                responseMapping.invalidate(request.getSeq());
                return RpcResponse.clientError(request.getSeq());
            }
            // 等待并获取响应
            response = (RpcResponse) responseFuture.waitForResponse();
        } while (retryTimes-- > 0 && response.isTimeout());
        return response;
    }

    @Override
    public <T> T invoke(String cmd, Object body, Class<T> resultType, HostAndPort node) {
        return invoke(cmd, body, resultType, Lists.newArrayList(node));
    }

    @Override
    public <T> T invoke(String cmd, Object body, Class<T> resultType, List<HostAndPort> nodes) {
        return invoke(cmd, body, resultType, nodes, 0);
    }

    @Override
    public <T> T invoke(String cmd, Object body, Class<T> resultType, List<HostAndPort> nodes, int retryTimes) {
        RpcResponse response = invoke(cmd, body, nodes, retryTimes);
        if (RpcResponse.SUCCESS_CODE.equals(response.getCode())) {
            String responseBody = response.getBody();
            return SerializerUtil.deserialize(responseBody, resultType);
        } else {
            logger.warn("The response code to this request {} is {}", response.getSeq(), response.getCode());
            return null;
        }
    }

    @Override
    public void invokeAsync(String cmd, Object body, RpcCallback callback, HostAndPort node) {
        invokeAsync(cmd, body, callback, Lists.newArrayList(node));
    }

    @Override
    public void invokeAsync(String cmd, Object body, RpcCallback callback, List<HostAndPort> nodes) {
        assertNodesNotNull(nodes);
        // 构造请求
        RpcRequest<?> request = buildRequest(cmd, body);
        // 发送请求
        try {
            sendCommand(request, nodes, callback, config.getRequestTimeout());
        } catch (Exception e) {
            // 未发送成功
            logger.error("Async send request error, requestId:{}", request.getSeq(), e);
            responseMapping.invalidate(request.getSeq());
        }
    }

    @Override
    public RpcResponse invokeWithRegistry(String cmd, Object body, String serviceName) {
        return invokeWithRegistry(cmd, body, serviceName, 0);
    }

    @Override
    public RpcResponse invokeWithRegistry(String cmd, Object body, String serviceName, int retryTimes) {
        registryConfigCheck();
        return invoke(cmd, body, discoverRpcService(serviceName), retryTimes);
    }

    @Override
    public <T> T invokeWithRegistry(String cmd, Object body, Class<T> resultType, String serviceName) {
        return invokeWithRegistry(cmd, body, resultType, serviceName, 0);
    }

    @Override
    public <T> T invokeWithRegistry(String cmd, Object body, Class<T> resultType, String serviceName, int retryTimes) {
        registryConfigCheck();
        return invoke(cmd, body, resultType, discoverRpcService(serviceName), retryTimes);
    }

    @Override
    public void invokeAsyncWithRegistry(String cmd, Object body, RpcCallback callback, String serviceName) {
        registryConfigCheck();
        invokeAsync(cmd, body, callback, discoverRpcService(serviceName));
    }

    private <T> RpcRequest<T> buildRequest(String cmd, T body) {
        RpcRequest<T> request = new RpcRequest<>();
        request.setSeq(IdGenerator.getId());
        if (StringUtils.isBlank(cmd)) {
            throw new IllegalArgumentException("cmd can not be null");
        }
        request.setCmd(cmd);
        request.setTs(System.currentTimeMillis());
        request.setBody(body);
        return request;
    }

    private ResponseFuture sendCommand(Command<?> command, List<HostAndPort> nodes,
                                       RpcCallback callback, Integer requestTimeout) {
        // 获取连接
        IConnection connection = getConnection(nodes, command);
        // 发送请求
        return sendCommand(command, connection, callback, requestTimeout);
    }

    private ResponseFuture sendCommand(Command<?> command, IConnection connection,
                                       RpcCallback callback, Integer requestTimeout) {
        ResponseFuture responseFuture =
                new ResponseFuture(command.getSeq(), requestTimeout, connection.getRemoteAddress(), callback);
        responseMapping.putResponseFuture(command.getSeq(), responseFuture);
        connection.send(command);
        return responseFuture;
    }

    private IConnection getConnection(List<HostAndPort> nodes, Command<?> command) {
        List<HostAndPort> availableNodes = nodeManager.chooseHANode(nodes);
        HostAndPort node = loadBalancer.selectNode(availableNodes, command);
        IConnection connection = nodeManager.getConnectionFromPool(node);
        if (connection == null) {
            throw new RpcException("No connection available, please try to add server node first!");
        }
        return connection;
    }

    private Command<String> buildHeartBeatPacket() {
        Command<String> ping = new Command<>();
        ping.setSeq(IdGenerator.getId());
        ping.setCommandType(CommandType.HEARTBEAT.getValue());
        ping.setBody(RpcConstant.PING);
        return ping;
    }

    private void initConfig() {
        nodeManager = new NodeManager(true, this, config.getConnectionSizePerNode());
        loadBalancer = LoadBalancerFactory.getLoadBalance(config.getLoadBalanceRule());
        responseMapping = new ResponseMapping(config.getRequestTimeout());
        if (config.isDeDuplicateEnable()) {
            buildDuplicatedMarker(config.getDuplicateCheckTime(), config.getDuplicateMaxSize());
        }
        //rpc服务健康检查
        Executors.newSingleThreadScheduledExecutor(SRpcThreadFactory.getDefault())
                .scheduleAtFixedRate(new NodeHealthCheckTask(nodeManager), 0, config.getServerHealthCheckTimeInterval(), TimeUnit.SECONDS);
    }

    private void assertNodesNotNull(List<HostAndPort> nodes) {
        if (CollectionUtils.isEmpty(nodes)) {
            throw new IllegalArgumentException("nodes can not be null");
        }
    }

    private List<HostAndPort> discoverRpcService(String serviceName) {
        List<HostAndPort> nodes;
        try {
            nodes = serviceDiscover.discoverRpcServiceAddress(serviceName);
        } catch (Exception e) {
            throw new RpcException("discover rpc service address failed", e);
        }
        return nodes;
    }

    private void registryConfigCheck() {
        if (this.registryConfig == null || !this.registryConfig.isEnableRegistry()) {
            throw new RpcException("Registry not configured");
        }
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
                pipeline.addLast("trafficShapingHandler", trafficShapingHandler);
            }
            //tls加密
            if (null != sslContext) {
                pipeline.addLast(defaultEventExecutorGroup, "sslHandler", sslContext.newHandler(ch.alloc()));
            }
            // 添加压缩编解码
            pipeline.addLast(
                    defaultEventExecutorGroup,
                    new ProtobufVarint32FrameDecoder(),
                    new JdkZlibExtendDecoder(),
                    new ProtobufDecoder(Rpc.Packet.getDefaultInstance()),
                    new ProtobufVarint32LengthFieldPrepender(),
                    new JdkZlibExtendEncoder(config.isCompressEnable(), config.getMinThreshold(), config.getMaxThreshold()),
                    new ProtobufEncoder(),

                    new IdleStateHandler(config.getConnectionIdleTime(), config.getConnectionIdleTime(), 0),
                    new NettyClientConnManageHandler(nodeManager),
                    new ClientProcessHandler(nodeManager, duplicatedMarker, responseMapping, config));
        }
    }
}
