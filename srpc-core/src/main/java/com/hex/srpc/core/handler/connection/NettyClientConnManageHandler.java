package com.hex.srpc.core.handler.connection;

import com.hex.srpc.core.node.INodeManager;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;

/**
 * @author: hs
 */
public class NettyClientConnManageHandler extends AbstractConnManagerHandler {
    private static final Logger logger = LoggerFactory.getLogger(NettyClientConnManageHandler.class);

    public NettyClientConnManageHandler(INodeManager nodeManager) {
        super.nodeManager = nodeManager;
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("rpc client connect to remote:{}", remoteAddress);
        }
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("rpc client disconnect!");
        }
        ctx.channel().close();
        super.disconnect(ctx, promise);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        close(ctx);
        super.close(ctx, promise);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state().equals(IdleState.ALL_IDLE)) {
                if (logger.isWarnEnabled()) {
                    logger.warn("netty rpc client channel idle {}", ctx.channel().remoteAddress());
                }
                ctx.channel().close();
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.channel().close();
        super.exceptionCaught(ctx, cause);
    }

}
