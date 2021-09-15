package com.hex.srpc.core.rpc.server;

import com.google.common.base.Throwables;
import com.hex.common.exception.RegistryException;
import com.hex.common.exception.RpcException;
import com.hex.common.net.HostAndPort;
import com.hex.common.spi.ExtensionLoader;
import com.hex.common.thread.SRpcThreadFactory;
import com.hex.common.utils.NetUtil;
import com.hex.publish.ServicePublisher;
import com.hex.srpc.core.config.SRpcServerConfig;
import com.hex.srpc.core.handler.NettyProcessHandler;
import com.hex.srpc.core.handler.NettyServerConnManagerHandler;
import com.hex.srpc.core.node.INodeManager;
import com.hex.srpc.core.node.NodeManager;
import com.hex.srpc.core.protocol.pb.proto.Rpc;
import com.hex.srpc.core.reflect.RouteScanner;
import com.hex.srpc.core.rpc.AbstractRpc;
import com.hex.srpc.core.rpc.Server;
import com.hex.srpc.core.rpc.compress.JdkZlibExtendDecoder;
import com.hex.srpc.core.rpc.compress.JdkZlibExtendEncoder;
import com.hex.srpc.core.rpc.task.ConnectionNumCountTask;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
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
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author hs
 */
public class SRpcServer extends AbstractRpc implements Server {

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();
    private Class<?> primarySource;
    private Set<String> scanPackages;
    private SRpcServerConfig serverConfig;
    private EventLoopGroup eventLoopGroupBoss;
    private EventLoopGroup eventLoopGroupSelector;
    private DefaultEventExecutorGroup defaultEventExecutorGroup;
    private INodeManager nodeManager = new NodeManager(false);
    private AtomicBoolean isServerStart = new AtomicBoolean(false);
    private ServicePublisher servicePublisher;
    private Integer port;

    public Server serverConfig(SRpcServerConfig config) {
        this.serverConfig = config;
        return this;
    }

    private SRpcServer() {
    }

    public static SRpcServer builder() {
        return new SRpcServer();
    }

    public Server sourceClass(Class<?> source) {
        primarySource = source;
        return this;
    }

    @Override
    public Server configScanPackages(Set<String> packages) {
        this.scanPackages = packages;
        return this;
    }

    @Override
    public Server configRegistry(String schema, List<String> registryAddress, String serviceName) {
        if (StringUtils.isBlank(serviceName)) {
            throw new RegistryException("serviceName can not be null");
        }
        setConfigRegistry(schema, registryAddress, serviceName);
        return this;
    }

    @Override
    public Server start() {
        if (isServerStart.compareAndSet(false, true)) {
            try {

                scanRpcServer();

                initServer();

                registryInitAndPublish();

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
    public Server port(int port) {
        NetUtil.checkPort(port);
        this.port = port;
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
            //清除注册中心节点
            clearRpcRegistryService();

        } catch (Exception e) {
            logger.error("RpcServer stop exception, {}", Throwables.getStackTraceAsString(e));
        }
        logger.info("RpcServer stop success");
    }

    private void initServer() {
        logger.info("RpcServer server init");
        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(1);
            this.eventLoopGroupSelector = new EpollEventLoopGroup(serverConfig.getSelectorThreads());
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(1);
            this.eventLoopGroupSelector = new NioEventLoopGroup(serverConfig.getSelectorThreads());
        }

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(serverConfig.getWorkerThreads());
        // 流控
        buildTrafficMonitor(defaultEventExecutorGroup,
                serverConfig.getTrafficMonitorEnable(), serverConfig.getMaxReadSpeed(), serverConfig.getMaxWriteSpeed());

        //tls加密
        if (serverConfig.getUseTLS() != null && serverConfig.getUseTLS()) {
            try {
                buildSSLContext(false, serverConfig);
            } catch (Exception e) {
                throw new RpcException("sRpcServer initialize SSLContext fail!", e);
            }
        }

        this.serverBootstrap.group(this.eventLoopGroupBoss, this.eventLoopGroupSelector)
                .channel(useEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 2048)
                .option(ChannelOption.SO_REUSEADDR, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.SO_KEEPALIVE, false)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_SNDBUF, serverConfig.getSendBuf())
                .childOption(ChannelOption.SO_RCVBUF, serverConfig.getReceiveBuf())
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK,
                        new WriteBufferWaterMark(serverConfig.getLowWaterLevel(), serverConfig.getHighWaterLevel()))
                .childHandler(new ServerChannel());

        if (useEpoll()) {
            this.serverBootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
        }

        Integer bindPort = this.port == null ? this.serverConfig.getPort() : this.port;

        try {
            this.serverBootstrap.bind(bindPort).sync();
        } catch (InterruptedException e) {
            throw new RpcException("RpcServer bind Interrupted!", e);
        }

        if (logger.isInfoEnabled()) {
            logger.info("RpcServer started success!  Listening port:{}", bindPort);
        }
    }

    private void scanRpcServer() {
        logger.info("RpcRouter scanning ...");
        new RouteScanner(this.primarySource)
                .setBasePackages(scanPackages)
                .san();
    }

    private void printConnectionNum() {
        if (serverConfig.getPrintConnectionNumInterval() != null && serverConfig.getPrintConnectionNumInterval() > 0) {
            Executors.newSingleThreadScheduledExecutor(SRpcThreadFactory.getDefault())
                    .scheduleAtFixedRate(new ConnectionNumCountTask(nodeManager), 5, 60, TimeUnit.SECONDS);
        }
    }

    private void registryInitAndPublish() {
        if (!checkRegistryEnable()) {
            return;
        }
        try {
            ExtensionLoader<ServicePublisher> loader = ExtensionLoader.getExtensionLoader(ServicePublisher.class);

            String registrySchema = this.registryConfig.getRegistrySchema();

            logger.info("use the registry schema: [{}]", registrySchema);

            this.servicePublisher = loader.getExtension(registrySchema);

            this.servicePublisher.initRegistry(this.registryConfig.getRegistryAddress());

        } catch (Exception e) {
            throw new RegistryException("serviceRegistry init failed", e);
        }

        //注册服务
        publishRpcService();
    }

    private void publishRpcService() {
        String serviceName = null;
        HostAndPort address = null;
        try {
            serviceName = this.registryConfig.getServiceName();
            address = NetUtil.getLocalHostAndPort(this.serverConfig.getPort());
            this.servicePublisher.publishRpcService(serviceName, address);
        } catch (Exception e) {
            logger.error("publish rpc service failed, serviceName: [{}], address: [{}]", serviceName, address);
        }
    }

    private void clearRpcRegistryService() {
        if (this.registryConfig != null && this.registryConfig.isEnableRegistry()) {
            HostAndPort address = null;
            try {
                address = NetUtil.getLocalHostAndPort(this.serverConfig.getPort());
                this.servicePublisher.clearRpcService(this.registryConfig.getServiceName(), address);
            } catch (Exception e) {
                logger.error("publish rpc service failed, serviceName: [{}], address: [{}]",
                        this.registryConfig.getServiceName(), address);
            }
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
            // 添加压缩编解码
            pipeline.addLast(
                    defaultEventExecutorGroup,
                    new ProtobufVarint32FrameDecoder(),
                    new JdkZlibExtendDecoder(),
                    new ProtobufDecoder(Rpc.Packet.getDefaultInstance()),
                    new ProtobufVarint32LengthFieldPrepender(),
                    new JdkZlibExtendEncoder(serverConfig.getCompressEnable(), serverConfig.getMinThreshold(), serverConfig.getMaxThreshold()),
                    new ProtobufEncoder(),

                    // 3min没收到或没发送数据则认为空闲
                    new IdleStateHandler(serverConfig.getConnectionIdleTime(), serverConfig.getConnectionIdleTime(), 0),
                    new NettyServerConnManagerHandler(nodeManager, serverConfig),
                    new NettyProcessHandler(nodeManager, serverConfig.getPreventDuplicateEnable(), serverConfig.getPrintHearBeatPacketInfo()));
        }
    }

}
