package com.hex.srpc.core.handler.process;

import com.hex.srpc.core.chain.DealingChain;
import com.hex.srpc.core.chain.DealingContext;
import com.hex.srpc.core.chain.dealing.DispatchDealing;
import com.hex.srpc.core.chain.dealing.DuplicateDealing;
import com.hex.srpc.core.config.SRpcServerConfig;
import com.hex.srpc.core.extension.DuplicatedMarker;
import com.hex.srpc.core.node.INodeManager;
import com.hex.srpc.core.protocol.adpater.PbProtocolAdapter;
import com.hex.srpc.core.protocol.pb.proto.Rpc;
import io.netty.channel.ChannelHandlerContext;

import static com.hex.srpc.core.connection.Connection.CONN;

/**
 * @author: hs
 */
public class ServerProcessHandler extends AbstractProcessHandler {

    private SRpcServerConfig config;

    public ServerProcessHandler(INodeManager nodeManager, DuplicatedMarker duplicatedMarker,
                                SRpcServerConfig config) {
        super(nodeManager, duplicatedMarker);
        this.config = config;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Rpc.Packet msg) throws Exception {
        // 生成处理责任链
        DealingChain chain = new DealingChain();
        if (duplicatedMarker != null) {
            chain.addDealing(new DuplicateDealing(duplicatedMarker));
        }
        chain.addDealing(new DispatchDealing());
        // 上下文，携带消息内容
        DealingContext context = new DealingContext();
        context.setCommand(PbProtocolAdapter.getAdapter().reverse(msg));
        context.setDealingChain(chain);
        context.setNodeManager(nodeManager);
        context.setConnection(ctx.channel().attr(CONN).get());
        context.setPrintHeartbeatInfo(config.getPrintHearBeatPacketInfo());
        // 开始执行责任链
        chain.deal(context);
    }

}
