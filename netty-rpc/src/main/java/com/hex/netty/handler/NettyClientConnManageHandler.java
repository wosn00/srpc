package com.hex.netty.handler;

import com.hex.netty.connection.DefaultConnectionManager;
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
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public NettyClientConnManageHandler(DefaultConnectionManager defaultConnectionManager) {
        super.defaultConnectionManager = defaultConnectionManager;
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        logger.info("rpc client connect to remote:[{}]", remoteAddress);
        super.connect(ctx, remoteAddress, localAddress, promise);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        logger.info("rpc client disconnect!");
        close(ctx);
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
                logger.warn("netty rpc client channel idle [{}]", ctx.channel().remoteAddress());
                close(ctx);
            }
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        close(ctx);
        super.exceptionCaught(ctx, cause);
    }

}
