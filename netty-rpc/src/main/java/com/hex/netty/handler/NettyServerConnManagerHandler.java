package com.hex.netty.handler;

import com.hex.netty.config.RpcServerConfig;
import com.hex.netty.connection.ConnectionManager;
import com.hex.netty.connection.NettyConnection;
import com.hex.netty.util.Util;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static com.hex.netty.connection.NettyConnection.CONN;

/**
 * @author: hs
 */
public class NettyServerConnManagerHandler extends AbstractConnManagerHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private RpcServerConfig serverConfig;

    public NettyServerConnManagerHandler(ConnectionManager connectionManager, RpcServerConfig serverConfig) {
        super.connectionManager = connectionManager;
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
        connectionManager.addConn(conn);
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
