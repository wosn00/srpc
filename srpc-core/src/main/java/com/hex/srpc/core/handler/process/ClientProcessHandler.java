package com.hex.srpc.core.handler.process;

import com.hex.srpc.core.chain.DealingChain;
import com.hex.srpc.core.chain.DealingContext;
import com.hex.srpc.core.chain.dealing.DispatchDealing;
import com.hex.srpc.core.chain.dealing.DuplicateDealing;
import com.hex.srpc.core.config.SRpcClientConfig;
import com.hex.srpc.core.extension.DuplicatedMarker;
import com.hex.srpc.core.invoke.ResponseMapping;
import com.hex.srpc.core.node.INodeManager;
import com.hex.srpc.core.protocol.adpater.PbProtocolAdapter;
import com.hex.srpc.core.protocol.pb.proto.Rpc;
import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.ExecutorService;

import static com.hex.srpc.core.connection.Connection.CONN;

/**
 * @author: hs
 */
public class ClientProcessHandler extends AbstractProcessHandler {

    private SRpcClientConfig config;
    private ResponseMapping responseMapping;

    public ClientProcessHandler(INodeManager nodeManager, DuplicatedMarker duplicatedMarker,
                                ResponseMapping responseMapping, SRpcClientConfig config, ExecutorService businessExecutor) {
        super(nodeManager, duplicatedMarker, businessExecutor);
        this.responseMapping = responseMapping;
        this.config = config;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Rpc.Packet msg) throws Exception {
        // 生成处理责任链
        DealingChain chain = new DealingChain();
        if (config.isDeDuplicateEnable()) {
            chain.addDealing(new DuplicateDealing(duplicatedMarker));
        }
        chain.addDealing(new DispatchDealing(responseMapping));
        // 上下文，携带消息内容
        DealingContext context = new DealingContext();
        context.setCommand(PbProtocolAdapter.getAdapter().reverse(msg));
        context.setDealingChain(chain);
        context.setNodeManager(nodeManager);
        context.setConnection(ctx.channel().attr(CONN).get());

        // 开始执行责任链
        if (businessExecutor != null) {
            businessExecutor.submit(() -> chain.deal(context));
        } else {
            chain.deal(context);
        }
    }

}
