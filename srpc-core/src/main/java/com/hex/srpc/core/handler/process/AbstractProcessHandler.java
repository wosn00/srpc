package com.hex.srpc.core.handler.process;

import com.google.common.base.Throwables;
import com.hex.srpc.core.extension.DuplicatedMarker;
import com.hex.srpc.core.node.INodeManager;
import com.hex.srpc.core.protocol.pb.proto.Rpc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hs
 */
public abstract class AbstractProcessHandler extends SimpleChannelInboundHandler<Rpc.Packet> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractProcessHandler.class);

    protected INodeManager nodeManager;
    protected DuplicatedMarker duplicatedMarker;

    public AbstractProcessHandler(INodeManager nodeManager, DuplicatedMarker duplicatedMarker) {
        this.nodeManager = nodeManager;
        this.duplicatedMarker = duplicatedMarker;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("SRpc exceptionCaught {}, {}", ctx.channel().remoteAddress(), Throwables.getStackTraceAsString(cause));
    }
}
