package com.hex.netty.handler;

import com.hex.netty.chain.DealingChain;
import com.hex.netty.chain.DealingContext;
import com.hex.netty.chain.dealing.DispatchDealing;
import com.hex.netty.chain.dealing.DuplicateDealing;
import com.hex.netty.cmd.IHandler;
import com.hex.netty.connection.ConnectionManager;
import com.hex.netty.protocol.adpater.PbProtocolAdapter;
import com.hex.netty.protocol.pb.proto.Rpc;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.List;

import static com.hex.netty.connection.NettyConnection.CONN;

/**
 * @author: hs
 */
public class NettyProcessHandler extends SimpleChannelInboundHandler<Rpc.Packet> {

    private ConnectionManager connectionManager;

    private List<IHandler> handlers;

    public NettyProcessHandler(ConnectionManager connectionManager, List<IHandler> handlers) {
        this.connectionManager = connectionManager;
        this.handlers = handlers;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Rpc.Packet msg) throws Exception {
        // 生成处理责任链
        DealingChain chain = new DealingChain();
        chain.addDealing(new DuplicateDealing());
        chain.addDealing(new DispatchDealing().registerHandlers(handlers));
        // 上下文，携带消息内容
        DealingContext context = new DealingContext();
        context.setCommand(PbProtocolAdapter.getAdapter().decode(msg));
        context.setDealingChain(chain);
        context.setConnectionManager(connectionManager);
        context.setConnection(ctx.channel().attr(CONN).get());
        // 开始执行责任链
        chain.deal(context);

    }

}
