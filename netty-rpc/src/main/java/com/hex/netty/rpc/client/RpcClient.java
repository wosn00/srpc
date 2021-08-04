package com.hex.netty.rpc.client;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.hex.netty.connection.ServerHealthCheckTask;
import com.hex.netty.constant.CommandType;
import com.hex.netty.exception.RpcException;
import com.hex.netty.protocol.Command;
import com.hex.netty.rpc.compress.JdkZlibExtendDecoder;
import com.hex.netty.rpc.compress.JdkZlibExtendEncoder;
import com.hex.netty.config.RpcClientConfig;
import com.hex.netty.connection.Connection;
import com.hex.netty.connection.ServerManager;
import com.hex.netty.connection.ServerManagerImpl;
import com.hex.netty.connection.NettyConnection;
import com.hex.netty.handler.NettyClientConnManageHandler;
import com.hex.netty.handler.NettyProcessHandler;
import com.hex.netty.invoke.RpcCallback;
import com.hex.netty.protocol.RpcRequest;
import com.hex.netty.protocol.RpcResponse;
import com.hex.netty.protocol.pb.proto.Rpc;
import com.hex.netty.rpc.AbstractRpc;
import com.hex.netty.rpc.Client;
import com.hex.netty.invoke.ResponseFuture;
import com.hex.netty.invoke.ResponseMapping;
import com.hex.netty.rpc.ext.HeartBeatTask;
import com.hex.netty.util.Util;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hex.netty.connection.NettyConnection.CONN;

/**
 * @author hs
 */
public class RpcClient extends AbstractRpc implements Client {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Bootstrap bootstrap = new Bootstrap();
    private RpcClientConfig config;
    private EventLoopGroup eventLoopGroupSelector;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private ServerManager serverManager;
    private AtomicBoolean isClientStart = new AtomicBoolean(false);

    private RpcClient() {
    }

    public Client config(RpcClientConfig config) {
        this.config = config;
        //初始化服务节点管理器
        initServerManager();
        return this;
    }

    public static RpcClient newBuilder() {
        return new RpcClient();
    }

    @Override
    public Client start() {
        if (isClientStart.compareAndSet(false, true)) {
            //启动客户端
            clientStart();
        } else {
            logger.warn("RpcClient already started!");
        }
        return this;
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
            serverManager.closeManager();
        } catch (Exception e) {
            logger.error("Failed to stop RpcClient!", e);
        }
        logger.info("RpcClient stop");
    }

    @Override
    public Client contact(List<InetSocketAddress> cluster) {
        try {
            serverManager.addCluster(cluster);
        } catch (Exception e) {
            logger.error("add server cluster failed, cluster:{}", cluster, e);
        }
        return this;
    }

    @Override
    public Client contact(InetSocketAddress node) {
        try {
            serverManager.addNode(node);
        } catch (Exception e) {
            logger.error("add server node failed, node:{}", node, e);
        }
        return this;
    }

    /**
     * 根据host port发起连接
     */
    @Override
    public Connection connect(String host, int port) {
        logger.info("RpcClient connect to host:[{}] port:[{}]", host, port);
        ChannelFuture future = this.bootstrap.connect(host, port);
        NettyConnection conn = null;
        if (future.awaitUninterruptibly(config.getConnectionTimeout())) {
            if (future.channel() != null && future.channel().isActive()) {
                conn = new NettyConnection(Util.genSeq(), future.channel());
                future.channel().attr(CONN).set(conn);
            } else {
                logger.error("RpcClient connect fail host:[{}] port:[{}]", host, port);
                // 记录失败次数
                ServerManagerImpl.serverError(new InetSocketAddress(host, port));
            }
        } else {
            logger.error("RpcClient connect fail host:[{}] port:[{}]", host, port);
            // 记录失败次数
            ServerManagerImpl.serverError(new InetSocketAddress(host, port));
        }
        return conn;
    }

    private void clientStart() {
        logger.info("RpcClient init ...");

        if (useEpoll()) {
            this.eventLoopGroupSelector = new EpollEventLoopGroup(config.getSelectorThreads());
        } else {
            this.eventLoopGroupSelector = new NioEventLoopGroup(config.getSelectorThreads());
        }
        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(config.getWorkerThreads());
        // 流控
        buildTrafficMonitor(defaultEventExecutorGroup, config.getTrafficMonitorEnable(), config.getMaxReadSpeed(), config.getMaxWriteSpeed());

        boolean useEpoll = useEpoll();
        this.bootstrap.group(this.eventLoopGroupSelector)
                .channel(useEpoll ? EpollSocketChannel.class : NioSocketChannel.class)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_KEEPALIVE, false)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.config.getConnectionTimeout())
                .option(ChannelOption.SO_SNDBUF, this.config.getSendBuf())
                .option(ChannelOption.SO_RCVBUF, this.config.getReceiveBuf())
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(this.config.getLowWaterLevel(), this.config.getHighWaterLevel()))
                .handler(new ClientChannel());

        // 心跳保活
        new Timer("HeartbeatTimer", true)
                .scheduleAtFixedRate(new HeartBeatTask(this.serverManager, this), 3 * 1000L, 30 * 1000L);
        logger.info("RpcClient init success!");
    }

    @Override
    public boolean sendHeartBeat(InetSocketAddress node) {
        Connection connection = getConnection(Lists.newArrayList(node));
        return sendHeartBeat(connection);
    }

    @Override
    public boolean sendHeartBeat(Connection connection) {
        //构造心跳包
        Command<String> heartBeatPacket = buildHeartBeatPacket();
        ResponseFuture responseFuture;
        //发送心跳
        try {
            responseFuture = sendCommand(heartBeatPacket, connection, null, 5);
        } catch (Exception e) {
            logger.error("sync send heartBeat packet error", e);
            return false;
        }
        ResponseMapping.putResponseFuture(heartBeatPacket.getSeq(), responseFuture);
        //等待并获取响应
        Command<String> command = responseFuture.waitForResponse();
        return "pong".equals(command.getBody());
    }


    @Override
    public RpcResponse invoke(String cmd, Object body) {
        return invoke(cmd, body, Collections.emptyList());
    }

    @Override
    public RpcResponse invoke(String cmd, Object body, List<InetSocketAddress> cluster) {
        // 构造请求
        RpcRequest request = buildRequest(cmd, body);
        ResponseFuture responseFuture;
        // 发送请求
        try {
            responseFuture = sendCommand(request, cluster, null, config.getRequestTimeout());
        } catch (Exception e) {
            // 未发送成功
            logger.error("sync send request error", e);
            return RpcResponse.clientError(request.getSeq());
        }
        ResponseMapping.putResponseFuture(request.getSeq(), responseFuture);

        // 等待并获取响应
        return (RpcResponse) responseFuture.waitForResponse();
    }

    @Override
    public <T> T invoke(String cmd, Object body, Class<T> resultType) {
        return invoke(cmd, body, resultType, Collections.emptyList());
    }

    @Override
    public <T> T invoke(String cmd, Object body, Class<T> resultType, List<InetSocketAddress> cluster) {
        RpcResponse response = invoke(cmd, body, cluster);
        if (RpcResponse.SUCCESS_CODE.equals(response.getCode())) {
            String responseBody = response.getBody();
            return JSON.parseObject(responseBody, resultType);
        } else {
            logger.warn("The response code to this request [{}] is [{}]", response.getSeq(), response.getCode());
            return null;
        }
    }

    @Override
    public void invokeAsync(String cmd, Object body) {
        invokeAsync(cmd, body, null, null);
    }

    @Override
    public void invokeAsync(String cmd, Object body, List<InetSocketAddress> cluster) {
        invokeAsync(cmd, body, null, cluster);
    }

    @Override
    public void invokeAsync(String cmd, Object body, RpcCallback callback) {
        invokeAsync(cmd, body, callback, Collections.emptyList());
    }

    @Override
    public void invokeAsync(String cmd, Object body, RpcCallback callback, List<InetSocketAddress> cluster) {
        // 构造请求
        RpcRequest request = buildRequest(cmd, body);
        ResponseFuture responseFuture = null;
        // 发送请求
        try {
            responseFuture = sendCommand(request, cluster, callback, config.getRequestTimeout());
        } catch (Exception e) {
            // 未发送成功
            logger.error("Async send request error, requestId:{}", request.getSeq(), e);
        }
        ResponseMapping.putResponseFuture(request.getSeq(), responseFuture);
    }

    private RpcRequest buildRequest(String cmd, Object body) {
        String requestBody = JSON.toJSONString(body);
        RpcRequest request = new RpcRequest();
        if (StringUtils.isBlank(request.getSeq())) {
            request.setSeq(Util.genSeq());
        }
        if (StringUtils.isBlank(cmd)) {
            throw new IllegalArgumentException("cmd can not be null");
        }
        request.setCmd(cmd);
        request.setTs(System.currentTimeMillis());
        request.setBody(requestBody);
        return request;
    }

    private ResponseFuture sendCommand(Command command, List<InetSocketAddress> nodes,
                                       RpcCallback callback, Integer requestTimeout) {
        // 获取连接
        Connection connection = getConnection(nodes);
        // 发送请求
        return sendCommand(command, connection, callback, requestTimeout);
    }

    private ResponseFuture sendCommand(Command command, Connection connection,
                                       RpcCallback callback, Integer requestTimeout) {
        connection.send(command);
        return new ResponseFuture(command.getSeq(), requestTimeout,
                (InetSocketAddress) connection.getRemoteAddress(), callback);
    }

    private Connection getConnection(List<InetSocketAddress> nodes) {
        Connection connection;
        if (CollectionUtils.isEmpty(nodes)) {
            // 选择默认集群节点连接
            connection = serverManager.chooseHAConnection();
        } else if (nodes.size() == 1) {
            // 不支持高可用
            connection = serverManager.chooseConnection(nodes.get(0));
        } else {
            // 支持高可用
            connection = serverManager.chooseHAConnection(nodes);
        }
        if (connection == null) {
            throw new RpcException("No connection available, please try to add server node first!");
        }
        return connection;
    }

    private Command<String> buildHeartBeatPacket() {
        Command<String> ping = new Command<>();
        ping.setSeq(Util.genSeq());
        ping.setCommandType(CommandType.HEARTBEAT.getValue());
        ping.setBody("ping");
        return ping;
    }

    private void initServerManager() {
        serverManager = new ServerManagerImpl(true, this, config.getConnectionSizePerHost(),
                config.getLoadBalanceRule());
        //服务健康检查
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new ServerHealthCheckTask(serverManager),
                5000, 60 * 1000L, TimeUnit.SECONDS);
    }

    /**
     * Rpc客户端channel
     */
    class ClientChannel extends ChannelInitializer<SocketChannel> {
        private final int idleTimeSeconds = 180;

        @Override
        public void initChannel(SocketChannel ch) {
            ChannelPipeline pipeline = ch.pipeline();
            // 流控
            if (null != trafficShapingHandler) {
                pipeline.addLast("trafficShapingHandler", trafficShapingHandler);
            }
            if (config.getCompressEnable() != null && config.getCompressEnable()) {
                // 添加压缩编解码
                pipeline.addLast(
                        defaultEventExecutorGroup,
                        new ProtobufVarint32FrameDecoder(),
                        new JdkZlibExtendDecoder(),
                        new ProtobufDecoder(Rpc.Packet.getDefaultInstance()),
                        new ProtobufVarint32LengthFieldPrepender(),
                        new JdkZlibExtendEncoder(config.getMinThreshold(), config.getMaxThreshold()),
                        new ProtobufEncoder());
            } else {
                //正常pb编解码
                pipeline.addLast(
                        defaultEventExecutorGroup,
                        new ProtobufVarint32FrameDecoder(),
                        new ProtobufDecoder(Rpc.Packet.getDefaultInstance()),
                        new ProtobufVarint32LengthFieldPrepender(),
                        new ProtobufEncoder());
            }
            pipeline.addLast(
                    defaultEventExecutorGroup,
                    // 3min内没收到或没发送数据则认为空闲
                    new IdleStateHandler(idleTimeSeconds, idleTimeSeconds, 0),
                    new NettyClientConnManageHandler(serverManager),
                    new NettyProcessHandler(serverManager, config.getPreventDuplicateEnable()));
        }
    }
}
