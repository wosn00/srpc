package com.hex.srpc.core.handler.process;

import com.hex.srpc.core.chain.DealingChain;
import com.hex.srpc.core.chain.DealingContext;
import com.hex.srpc.core.chain.dealing.DispatchDealing;
import com.hex.srpc.core.config.SRpcClientConfig;
import com.hex.srpc.core.invoke.ResponseMapping;
import com.hex.srpc.core.node.INodeManager;
import com.hex.srpc.core.protocol.Command;
import com.hex.srpc.core.protocol.RpcResponse;
import io.netty.channel.ChannelHandlerContext;

import static com.hex.srpc.core.connection.Connection.CONN;

/**
 * @author: hs
 */
public class ClientProcessHandler extends AbstractProcessHandler {

    private SRpcClientConfig config;
    private ResponseMapping responseMapping;

    public ClientProcessHandler(INodeManager nodeManager, ResponseMapping responseMapping, SRpcClientConfig config) {
        super(nodeManager);
        this.responseMapping = responseMapping;
        this.config = config;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Command command) throws Exception {
        // 生成处理责任链
        DealingChain chain = new DealingChain();

        chain.addDealing(new DispatchDealing(responseMapping));
        // 上下文，携带消息内容
        DealingContext context = new DealingContext();
        context.setClient(true);
        context.setCommand(command);
        context.setDealingChain(chain);
        context.setNodeManager(nodeManager);
        context.setConnection(ctx.channel().attr(CONN).get());

        // 开始执行责任链
        chain.deal(context);
    }

}
