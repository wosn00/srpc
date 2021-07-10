package com.hex.netty.chain.dealing;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hex.netty.chain.Dealing;
import com.hex.netty.chain.DealingContext;
import com.hex.netty.constant.CommandType;
import com.hex.netty.protocol.Command;
import com.hex.netty.protocol.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * @author: hs
 * 去重处理器，集群部署模式需使用redis实现去重
 */
public class DuplicateDealing implements Dealing {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static Cache<String, Boolean> duplicateCache = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofSeconds(300))
            .build();

    @Override
    public void deal(DealingContext context) {
        Command<String> command = context.getCommand();
        // 只有是请求才需要去重
        if (CommandType.REQUEST_COMMAND.getValue().equals(command.getCommandType())) {
            String seq = command.getSeq();
            Boolean seqPresent = duplicateCache.getIfPresent(seq);
            if (seqPresent == null) {
                duplicateCache.put(seq, Boolean.TRUE);
                context.nextDealing();
            } else {
                logger.warn("Received duplicate request seq=[{}], ignore it", seq);
                RpcResponse response = RpcResponse.duplicateRequest(seq);
                context.getConnection().send(response);
            }
        } else {
            context.nextDealing();
        }
    }
}
