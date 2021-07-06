package com.hex.netty.rpc.server;

import com.google.common.base.Throwables;
import com.hex.netty.cmd.IHadnler;
import com.hex.netty.config.RpcServerConfig;
import com.hex.netty.connection.DefaultConnectionManager;
import com.hex.netty.exception.RpcException;
import com.hex.netty.handler.NettyClientConnManageHandler;
import com.hex.netty.handler.NettyProcessHandler;
import com.hex.netty.handler.NettyServerConnManagerHandler;
import com.hex.netty.protocol.pb.proto.Rpc;
import com.hex.netty.rpc.AbstractRpc;
import com.hex.netty.rpc.Server;
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

import java.util.List;

/**
 * @author hs
 */
public class RpcServer extends AbstractRpc implements Server {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ServerBootstrap serverBootstrap = new ServerBootstrap();

    private RpcServerConfig config;

    private EventLoopGroup eventLoopGroupBoss;

    private EventLoopGroup eventLoopGroupSelector;

    private DefaultEventExecutorGroup defaultEventExecutorGroup;

    private DefaultConnectionManager defaultConnectionManager = new DefaultConnectionManager();

    public RpcServer(RpcServerConfig config, List<IHadnler> hadnlers) {
        this.config = config;
        super.hadnlers = hadnlers;
    }

    @Override
    public void start() {
        logger.info("rpc server init ...");

        if (useEpoll()) {
            this.eventLoopGroupBoss = new EpollEventLoopGroup(1);
            this.eventLoopGroupSelector = new EpollEventLoopGroup(config.getEventLoopGroupSelector());
        } else {
            this.eventLoopGroupBoss = new NioEventLoopGroup(1);
            this.eventLoopGroupSelector = new NioEventLoopGroup(config.getEventLoopGroupSelector());
        }

        this.defaultEventExecutorGroup = new DefaultEventExecutorGroup(config.getWorkerThreads());

//        buildTrafficMonitor(defaultEventExecutorGroup, config.getTrafficMonitorEnable(), config.getMaxReadSpeed(), config.getMaxWriteSpeed());

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
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
//                        // 流控
//                        if (null != trafficShapingHandler) {
//                            pipeline.addLast("trafficShapingHandler", trafficShapingHandler);
//                        }
                        pipeline.addLast(
                                defaultEventExecutorGroup,
                                new ProtobufVarint32FrameDecoder(),
                                new ProtobufDecoder(Rpc.Packet.getDefaultInstance()),
                                new ProtobufVarint32LengthFieldPrepender(),
                                new ProtobufEncoder(),
                                // 指定时间内没收到或没发送数据则认为空闲
                                new IdleStateHandler(config.getMaxIdleSecs(), config.getMaxIdleSecs(), 0),
                                new NettyServerConnManagerHandler(defaultConnectionManager, config),
                                new NettyProcessHandler(config.getProtocolAdapter(), defaultConnectionManager, hadnlers));
                    }
                });
        if (useEpolll) {
            this.serverBootstrap.option(EpollChannelOption.EPOLL_MODE, EpollMode.EDGE_TRIGGERED);
        }

        try {
            this.serverBootstrap.bind(this.config.getPort()).sync();
        } catch (InterruptedException e1) {
            throw new RpcException("NettyRpcServer bind Interrupted!", e1);
        }

        logger.info("NettyRpcServer started success!  Listening port:{}", config.getPort());

    }

    @Override
    public void stop() {
        logger.info("NettyRpcServer stopping...");
        try {
            // 关闭连接管理器
            defaultConnectionManager.close();

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
            logger.error("NettyRpcServer stop exception, {}", Throwables.getStackTraceAsString(e));
        }
        logger.info("NettyRpcServer stopped!");
    }
}
