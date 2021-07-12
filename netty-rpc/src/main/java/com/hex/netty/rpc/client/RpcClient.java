package com.hex.netty.rpc.client;

import com.alibaba.fastjson.JSON;
import com.hex.netty.constant.CommandType;
import com.hex.netty.rpc.compress.JdkZlibExtendDecoder;
import com.hex.netty.rpc.compress.JdkZlibExtendEncoder;
import com.hex.netty.config.RpcClientConfig;
import com.hex.netty.connection.Connection;
import com.hex.netty.connection.ConnectionManager;
import com.hex.netty.connection.DefaultConnectionManager;
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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
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

    private ConnectionManager connectionManager = new DefaultConnectionManager();

    private AtomicBoolean isClientStart = new AtomicBoolean(false);

    private RpcClient() {
    }

    public RpcClient config(RpcClientConfig config) {
        this.config = config;
        return this;
    }

    public static RpcClient newBuilder() {
        return new RpcClient();
    }


    @Override
    public Client start() {
        if (isClientStart.compareAndSet(false, true)) {
            clientStart();
        } else {
            logger.warn("RpcClient has started!");
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
            // 关闭连接
            connectionManager.close();
        } catch (Exception e) {
            logger.error("Failed to stop RpcClient!", e);
        }
        logger.info("RpcClient stop");

    }

    @Override
    public Connection connect(String host, int port) {
        logger.info("RpcClient connect to host:[{}] port:[{}]", host, port);
        ChannelFuture future = this.bootstrap.connect(host, port);
        NettyConnection conn = null;
        if (future.awaitUninterruptibly(config.getConnectionTimeout())) {
            if (future.channel() != null && future.channel().isActive()) {
                conn = new NettyConnection(Util.genSeq(), future.channel());
                future.channel().attr(CONN).set(conn);
                connectionManager.addConn(conn);
            } else {
                logger.error("RpcClient connect fail host:[{}] port:[{}]", host, port);
            }
        } else {
            logger.error("RpcClient connect fail host:[{}] port:[{}]", host, port);
        }
        return conn;
    }

    @Override
    public void connect(String host, int port, int connectionNum) {
        if (connectionNum <= 0) {
            throw new IllegalArgumentException("The number of connections should be greater than 1 !");
        }
        for (int i = 0; i < connectionNum; i++) {
            connect(host, port);
        }
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
                .handler(new RpcClientChannel());

        // 心跳保活
        new Timer("HeartbeatTimer", true)
                .scheduleAtFixedRate(new HeartBeatTask(this.connectionManager), 3 * 1000L, 30 * 1000L);
        logger.info("RpcClient init success!");
    }


    @Override
    public RpcResponse invoke(String cmd, Object body) {
        RpcRequest request = buildRequest(cmd, body);
        // 发送请求
        if (!sendRequest(request)) {
            // 未发送成功
            return RpcResponse.clientError(request.getSeq());
        }
        ResponseFuture responseFuture = new ResponseFuture(request.getSeq(), config.getRequestTimeout());
        ResponseMapping.putResponseFuture(request.getSeq(), responseFuture);

        // 等待并获取响应
        return responseFuture.waitForResponse();
    }

    @Override
    public <T> T invoke(String cmd, Object body, Class<T> resultType) {
        RpcResponse response = invoke(cmd, body);
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
        invokeAsync(cmd, body, null);
    }

    @Override
    public void invokeAsync(String cmd, Object body, RpcCallback callback) {
        RpcRequest request = buildRequest(cmd, body);
        // 发送请求
        sendRequest(request);
        // 添加响应回调
        ResponseFuture responseFuture = new ResponseFuture(request.getSeq(), callback);
        ResponseMapping.putResponseFuture(request.getSeq(), responseFuture);
    }

    private RpcRequest buildRequest(String cmd, Object body) {
        String requestBody = JSON.toJSONString(body);
        RpcRequest request = new RpcRequest();
        if (StringUtils.isBlank(request.getSeq())) {
            request.setSeq(Util.genSeq());
        }
        if (StringUtils.isBlank(cmd)) {
            throw new IllegalArgumentException("cmd can not be empty");
        }
        request.setCmd(cmd);
        request.setTs(System.currentTimeMillis());
        request.setBody(requestBody);
        request.setCommandType(CommandType.REQUEST_COMMAND.getValue());
        return request;
    }

    private boolean sendRequest(RpcRequest rpcRequest) {
        // 获取连接
        Connection conn = connectionManager.getConn();
        if (conn == null) {
            logger.error("No connection available, please try to connect RpcServer first!");
            return false;
        }
        // 发送请求
        conn.send(rpcRequest);
        return true;
    }

    /**
     * Rpc客户端channel
     */
    class RpcClientChannel extends ChannelInitializer<SocketChannel> {
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
                    new IdleStateHandler(0, 0, 180),
                    new NettyClientConnManageHandler(connectionManager),
                    new NettyProcessHandler(connectionManager, config.getPreventDuplicateEnable()));
        }
    }
}
