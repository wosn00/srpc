package com.hex.srpc.core.chain.dealing;

import com.hex.srpc.core.chain.Dealing;
import com.hex.srpc.core.chain.DealingContext;
import com.hex.srpc.core.extension.DuplicatedMarker;
import com.hex.srpc.core.protocol.Command;
import com.hex.srpc.core.protocol.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hs
 * <p>
 * 去重处理器，集群部署模式需使用redis实现去重
 */
public class DuplicateDealing implements Dealing {
    private static final Logger logger = LoggerFactory.getLogger(DuplicateDealing.class);

    private DuplicatedMarker duplicatedMarker;

    public DuplicateDealing(DuplicatedMarker duplicatedMarker) {
        this.duplicatedMarker = duplicatedMarker;
    }

    @Override
    public void deal(DealingContext context) {
        Command command = context.getCommand();
        // 只有收到请求才需要去重
        if (command.isRequest()) {
            Long seq = command.getSeq();
            if (duplicatedMarker.mark(seq)) {
                logger.warn("Received duplicate request seq：[{}], ignore it", seq);
                RpcResponse response = RpcResponse.duplicateRequest(seq);
                context.getConnection().send(response);
            } else {
                context.nextDealing();
            }
        } else {
            context.nextDealing();
        }
    }
}
