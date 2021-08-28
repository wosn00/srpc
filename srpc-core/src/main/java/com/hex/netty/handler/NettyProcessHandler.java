package com.hex.netty.handler;

import com.google.common.base.Throwables;
import com.hex.netty.chain.DealingChain;
import com.hex.netty.chain.DealingContext;
import com.hex.netty.chain.dealing.DispatchDealing;
import com.hex.netty.chain.dealing.DuplicateDealing;
import com.hex.netty.node.INodeManager;
import com.hex.netty.protocol.adpater.PbProtocolAdapter;
import com.hex.netty.protocol.pb.proto.Rpc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hex.netty.connection.Connection.CONN;

/**
 * @author: hs
 */
public class NettyProcessHandler extends SimpleChannelInboundHandler<Rpc.Packet> {
    private static final Logger logger = LoggerFactory.getLogger(NettyProcessHandler.class);

    private INodeManager nodeManager;

    private boolean enablePreventDuplicate;

    public NettyProcessHandler(INodeManager nodeManager, boolean enablePreventDuplicate) {
        this.nodeManager = nodeManager;
        this.enablePreventDuplicate = enablePreventDuplicate;
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
        context.setCommand(PbProtocolAdapter.getAdapter().decode(msg));
        context.setDealingChain(chain);
        context.setNodeManager(nodeManager);
        context.setConnection(ctx.channel().attr(CONN).get());
        // 开始执行责任链
        chain.deal(context);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Rpc exceptionCaught {}, {}", ctx.channel().remoteAddress(), Throwables.getStackTraceAsString(cause));
    }

}
