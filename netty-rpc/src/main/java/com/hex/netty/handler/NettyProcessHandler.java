package com.hex.netty.handler;

import com.hex.netty.chain.DealingChain;
import com.hex.netty.chain.DealingContext;
import com.hex.netty.chain.dealing.DispatchDealing;
import com.hex.netty.chain.dealing.DuplicateDealing;
import com.hex.netty.cmd.IHadnler;
import com.hex.netty.connection.ConnectionManager;
import com.hex.netty.protocol.Command;
import com.hex.netty.protocol.adpater.ProtocolAdapter;
import com.hex.netty.protocol.pb.proto.Rpc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

/**
 * @author: hs
 */
public class NettyProcessHandler extends SimpleChannelInboundHandler<Rpc.Packet> {

    private ProtocolAdapter<Command, Rpc.Packet> protocolAdapter;

    private ConnectionManager connectionManager;

    private List<IHadnler> hadnlers;

    public NettyProcessHandler(ProtocolAdapter protocolAdapter, ConnectionManager connectionManager, List<IHadnler> hadnlers) {
        this.protocolAdapter = protocolAdapter;
        this.connectionManager = connectionManager;
        this.hadnlers = hadnlers;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Rpc.Packet msg) throws Exception {
        // 生成处理责任链
        DealingChain chain = new DealingChain();
        chain.addDealing(new DuplicateDealing());
        chain.addDealing(new DispatchDealing().registerHandlers(hadnlers));
        // 上下文，携带消息内容
        DealingContext context = new DealingContext();
        context.setCommand(protocolAdapter.decode(msg));
        context.setDealingChain(chain);
        context.setConnectionManager(connectionManager);
        // 开始执行责任链
        chain.deal(context);

    }

}
