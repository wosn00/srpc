package com.hex.netty.rpc.server;

import com.google.common.base.Throwables;
import com.hex.netty.reflection.RouteScanner;
import com.hex.netty.rpc.compress.JdkZlibExtendDecoder;
import com.hex.netty.rpc.compress.JdkZlibExtendEncoder;
import com.hex.netty.config.RpcServerConfig;
import com.hex.netty.connection.Connection;
import com.hex.netty.connection.ConnectionManager;
import com.hex.netty.connection.DefaultConnectionManager;
import com.hex.netty.exception.RpcException;
import com.hex.netty.handler.NettyProcessHandler;
import com.hex.netty.handler.NettyServerConnManagerHandler;
import com.hex.netty.protocol.pb.proto.Rpc;
import com.hex.netty.rpc.AbstractRpc;
import com.hex.netty.rpc.Server;
import com.hex.netty.util.Util;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollMode;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author hs
 */
public class RpcServer extends AbstractRpc implements Server {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    private Class<?> primarySource;

    private RpcServerConfig config;

    private EventLoopGroup eventLoopGroupBoss;

    private EventLoopGroup eventLoopGroupSelector;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    private ConnectionManager connectionManager = new DefaultConnectionManager();

    private AtomicBoolean isServerStart = new AtomicBoolean(false);

    public RpcServer config(RpcServerConfig config) {
        this.config = config;
        return this;
    }

    private RpcServer() {
    }

    public static RpcServer newBuilder() {
        return new RpcServer();
    }

    public RpcServer source(Class<?> source) {
        primarySource = source;
        return this;
    }

    @Override
    public Server start() {
        if (isServerStart.compareAndSet(false, true)) {
            serverStart();
        } else {
            logger.warn("RpcServer has started!");
        }
        return this;
    }

    @Override
    public Server startAtPort(int port) {
        config.setPort(port);
        start();
        return this;
    }

    @Override
    public void stop() {
        if (!isServerStart.get()) {
            logger.warn("RpcServer does not start");
            return;
        }
        logger.info("RpcServer stopping...");
        try {
            // 关闭连接管理器
            connectionManager.close();

            if (this.defaultEventExecutorGroup != null) {
                this.defaultEventExecutorGroup.shutdownGracefully();
            }
            if (this.eventLoopGroupSelector != null) {
                this.eventLoopGroupSelector.shutdownGracefully();
            }
            if (this.eventLoopGroupBoss != null) {
                this.eventLoopGroupBoss.shutdownGracefully();
            }
        } catch (Exception e) {
            logger.error("RpcServer stop exception, {}", Throwables.getStackTraceAsString(e));
        }
        logger.info("RpcServer stopped!");
    }

    private void serverStart() {
        logger.info("RpcRouter scanning...");
        new RouteScanner(this.primarySource).san();

        logger.info("RpcServer server init ...");

        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(1);
            this.eventLoopGroupSelector = new EpollEventLoopGroup(config.getSelectorThreads());
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(1);
            this.eventLoopGroupSelector = new NioEventLoopGroup(config.getSelectorThreads());
        }

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(config.getWorkerThreads());
        // 流控
        buildTrafficMonitor(defaultEventExecutorGroup, config.getTrafficMonitorEnable(), config.getMaxReadSpeed(), config.getMaxWriteSpeed());

        boolean useEpolll = useEpoll();
        this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector)
                .channel(useEpolll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 2048)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, config.getSendBuf())
                .childOption(ChannelOption.SO_RCVBUF, config.getReceiveBuf())
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(config.getLowWaterLevel(), config.getHighWaterLevel()))
                .childHandler(new RpcServerChannel());

        if (useEpolll) {
            this.serverBootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
        }

        try {
            this.serverBootstrap.bind(this.config.getPort()).sync();
        } catch (InterruptedException e) {
            throw new RpcException("RpcServer bind Interrupted!", e);
        }

        logger.info("RpcServer started success!  Listening port:{}", config.getPort());
        countConnectionNum(connectionManager);

    }

    private void countConnectionNum(ConnectionManager connectionManager) {
        new Timer("connection monitor", true)
                .scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        int size = connectionManager.size();
                        Connection[] allConn = connectionManager.getAllConn();
                        Map<String, Object> connMap = new HashMap<>();
                        for (int i = 0; i < allConn.length; i++) {
                            connMap.put("connection" + (i + 1), allConn[i].getRemoteAddress());
                        }
                        if (connMap.size() == 0) {
                            logger.info("服务端当前连接数:[0]");
                        } else {
                            logger.info("服务端当前连接数量:[{}], 客户端地址:[{}]", size, Util.jsonSerializePretty(connMap));
                        }
                    }
                }, 3 * 1000L, 60 * 1000L);
    }


    /**
     * RPC服务端channel
     */
    class RpcServerChannel extends ChannelInitializer<SocketChannel> {
        @Override
        public void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            // 流控
            if (null != trafficShapingHandler) {
                pipeline.addLast("trafficShapingHandler", trafficShapingHandler);
            }
            // 编解码
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
                    // 3min没收到或没发送数据则认为空闲
                    new IdleStateHandler(0, 0, 180),
                    new NettyServerConnManagerHandler(connectionManager, config),
                    new NettyProcessHandler(connectionManager, config.getPreventDuplicateEnable()));
        }
    }

}
