package com.hex.netty.handler;

import com.hex.netty.config.RpcServerConfig;
import com.hex.netty.connection.ConnectionPool;
import com.hex.netty.connection.ServerManager;
import com.hex.netty.connection.NettyConnection;
import com.hex.netty.util.Util;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static com.hex.netty.connection.NettyConnection.CONN;

/**
 * @author: hs
 */
public class NettyServerConnManagerHandler extends AbstractConnManagerHandler {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerConnManagerHandler.class);

    private RpcServerConfig serverConfig;

    public NettyServerConnManagerHandler(ServerManager serverManager, RpcServerConfig serverConfig) {
        super.serverManager = serverManager;
        this.serverConfig = serverConfig;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel register");
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        logger.info("channel unregister");
        super.channelUnregistered(ctx);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        close(ctx);
        super.close(ctx, promise);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        NettyConnection conn = new NettyConnection(Util.genSeq(), ctx.channel());
        InetSocketAddress node = (InetSocketAddress) ctx.channel().remoteAddress();
        serverManager.addNode(node);
        ConnectionPool connectionPool = serverManager.getConnectionPool(node);
        if (connectionPool != null) {
            connectionPool.addConnection(conn);
        } else {
            logger.warn("connectionPool is null, node:{}", node);
        }
        ctx.channel().attr(CONN).set(conn);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.warn("channel inactive");
        ctx.channel().close();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        super.exceptionCaught(ctx, cause);
    }
}
