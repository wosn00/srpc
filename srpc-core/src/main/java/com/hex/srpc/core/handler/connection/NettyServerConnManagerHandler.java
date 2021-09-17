package com.hex.srpc.core.handler.connection;

import com.hex.srpc.core.config.SRpcServerConfig;
import com.hex.srpc.core.connection.Connection;
import com.hex.srpc.core.connection.IConnectionPool;
import com.hex.common.id.IdGenerator;
import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.node.INodeManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static com.hex.srpc.core.connection.Connection.CONN;

/**
 * @author: hs
 */
public class NettyServerConnManagerHandler extends AbstractConnManagerHandler {
    private static final Logger logger = LoggerFactory.getLogger(NettyServerConnManagerHandler.class);

    private SRpcServerConfig serverConfig;

    public NettyServerConnManagerHandler(INodeManager nodeManager, SRpcServerConfig serverConfig) {
        super.nodeManager = nodeManager;
        this.serverConfig = serverConfig;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("channel register");
        }
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("channel unregister");
        }
        super.channelUnregistered(ctx);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        close(ctx);
        super.close(ctx, promise);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Connection conn = new Connection(IdGenerator.getId(), ctx.channel());
        HostAndPort node = HostAndPort.from((InetSocketAddress) ctx.channel().remoteAddress());
        nodeManager.addNode(node);
        IConnectionPool connectionPool = nodeManager.getConnectionPool(node);
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
        if (logger.isDebugEnabled()) {
            logger.debug("channel inactive");
        }
        ctx.channel().close();
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        super.exceptionCaught(ctx, cause);
    }
}
