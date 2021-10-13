package com.hex.srpc.core.handler.process;

import com.google.common.base.Throwables;
import com.hex.srpc.core.node.INodeManager;
import com.hex.srpc.core.protocol.Command;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hs
 */
public abstract class AbstractProcessHandler extends SimpleChannelInboundHandler<Command> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractProcessHandler.class);

    protected INodeManager nodeManager;

    public AbstractProcessHandler(INodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("SRpc exceptionCaught {}, {}", ctx.channel().remoteAddress(), Throwables.getStackTraceAsString(cause));
    }
}
