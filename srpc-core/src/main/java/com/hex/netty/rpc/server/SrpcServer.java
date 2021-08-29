package com.hex.netty.rpc.server;

import com.google.common.base.Throwables;
import com.hex.netty.config.RpcServerConfig;
import com.hex.netty.config.RpcThreadFactory;
import com.hex.netty.exception.RpcException;
import com.hex.netty.handler.NettyProcessHandler;
import com.hex.netty.handler.NettyServerConnManagerHandler;
import com.hex.netty.node.INodeManager;
import com.hex.netty.node.NodeManager;
import com.hex.netty.protocol.pb.proto.Rpc;
import com.hex.netty.reflect.RouteScanner;
import com.hex.netty.rpc.AbstractRpc;
import com.hex.netty.rpc.Server;
import com.hex.netty.rpc.compress.JdkZlibExtendDecoder;
import com.hex.netty.rpc.compress.JdkZlibExtendEncoder;
import com.hex.netty.rpc.task.ConnectionNumCountTask;
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

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author hs
 */
public class SrpcServer extends AbstractRpc implements Server {

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private Class<?> primarySource;
    private Set<String> scanPackages;
    private RpcServerConfig config;
    private EventLoopGroup eventLoopGroupBoss;
    private EventLoopGroup eventLoopGroupSelector;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private INodeManager nodeManager = new NodeManager(false);
    private AtomicBoolean isServerStart = new AtomicBoolean(false);

    public SrpcServer config(RpcServerConfig config) {
        this.config = config;
        return this;
    }

    private SrpcServer() {
    }

    public static SrpcServer builder() {
        return new SrpcServer();
    }

    public SrpcServer source(Class<?> source) {
        primarySource = source;
        return this;
    }

    @Override
    public Server configScanPackages(Set<String> packages) {
        this.scanPackages = packages;
        return this;
    }

    @Override
    public Server start() {
        if (isServerStart.compareAndSet(false, true)) {
            try {
                scanRpcServer();
                serverStart();
                printConnectionNum();
                registerShutdownHook(this::stop);
            } catch (Exception e) {
                logger.error("RpcServer started failed");
                throw e;
            }
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
            nodeManager.closeManager();

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
        logger.info("RpcServer stop success");
    }

    private void serverStart() {
        logger.info("RpcServer server init");
        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(1);
            this.eventLoopGroupSelector = new EpollEventLoopGroup(config.getSelectorThreads());
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(1);
            this.eventLoopGroupSelector = new NioEventLoopGroup(config.getSelectorThreads());
        }

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(config.getWorkerThreads());
        // 流控
        buildTrafficMonitor(defaultEventExecutorGroup,
                config.getTrafficMonitorEnable(), config.getMaxReadSpeed(), config.getMaxWriteSpeed());

        //tls加密
        if (config.getUseTLS() != null && config.getUseTLS()) {
            try {
                buildSSLContext(false, config);
            } catch (Exception e) {
                throw new RpcException("sRpcServer initialize SSLContext fail!", e);
            }
        }

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
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
                        new WriteBufferWaterMark(config.getLowWaterLevel(), config.getHighWaterLevel()))
                .childHandler(new ServerChannel());

        if (useEpolll) {
            this.serverBootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
        }

        try {
            this.serverBootstrap.bind(this.config.getPort()).sync();
        } catch (InterruptedException e) {
            throw new RpcException("RpcServer bind Interrupted!", e);
        }

        logger.info("RpcServer started success!  Listening port:{}", config.getPort());
    }

    private void scanRpcServer() {
        logger.info("RpcRouter scanning ...");
        new RouteScanner(this.primarySource)
                .setBasePackages(scanPackages)
                .san();
    }

    private void printConnectionNum() {
        if (config.getPrintConnectionNumInterval() != null && config.getPrintConnectionNumInterval() > 0) {
            Executors.newSingleThreadScheduledExecutor(RpcThreadFactory.getDefault())
                    .scheduleAtFixedRate(new ConnectionNumCountTask(nodeManager), 5, 60, TimeUnit.SECONDS);
        }
    }

    /**
     * RPC服务端channel
     */
    class ServerChannel extends ChannelInitializer<SocketChannel> {

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
                    new IdleStateHandler(config.getConnectionIdleTime(), config.getConnectionIdleTime(), 0),
                    new NettyServerConnManagerHandler(nodeManager, config),
                    new NettyProcessHandler(nodeManager, config.getPreventDuplicateEnable(), config.getPrintHearBeatPacketInfo()));
        }
    }

}
