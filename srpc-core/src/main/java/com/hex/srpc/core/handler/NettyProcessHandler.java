package com.hex.srpc.core.handler;

import com.google.common.base.Throwables;
import com.hex.srpc.core.chain.DealingChain;
import com.hex.srpc.core.chain.DealingContext;
import com.hex.srpc.core.chain.dealing.DispatchDealing;
import com.hex.srpc.core.chain.dealing.DuplicateDealing;
import com.hex.srpc.core.node.INodeManager;
import com.hex.srpc.core.protocol.adpater.PbProtocolAdapter;
import com.hex.srpc.core.protocol.pb.proto.Rpc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hex.srpc.core.connection.Connection.CONN;

/**
 * @author: hs
 */
public class NettyProcessHandler extends SimpleChannelInboundHandler<Rpc.Packet> {
    private static final Logger logger = LoggerFactory.getLogger(NettyProcessHandler.class);

    private INodeManager nodeManager;
    private boolean enablePreventDuplicate;
    private boolean isPrintHearBeatInfo;

    public NettyProcessHandler(INodeManager nodeManager, boolean enablePreventDuplicate) {
        this.nodeManager = nodeManager;
        this.enablePreventDuplicate = enablePreventDuplicate;
    }

    public NettyProcessHandler(INodeManager nodeManager, boolean enablePreventDuplicate, boolean isPrintHearBeatInfo) {
        this.nodeManager = nodeManager;
        this.enablePreventDuplicate = enablePreventDuplicate;
        this.isPrintHearBeatInfo = isPrintHearBeatInfo;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Rpc.Packet msg) throws Exception {
        // 生成处理责任链
        DealingChain chain = new DealingChain();
        if (enablePreventDuplicate) {
            chain.addDealing(new DuplicateDealing());
        }
        chain.addDealing(new DispatchDealing());
        // 上下文，携带消息内容
        DealingContext context = new DealingContext();
        context.setCommand(PbProtocolAdapter.getAdapter().reverse(msg));
        context.setDealingChain(chain);
        context.setNodeManager(nodeManager);
        context.setConnection(ctx.channel().attr(CONN).get());
        context.setPrintHeartbeatInfo(isPrintHearBeatInfo);
        // 开始执行责任链
        chain.deal(context);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Rpc exceptionCaught {}, {}", ctx.channel().remoteAddress(), Throwables.getStackTraceAsString(cause));
    }

}
