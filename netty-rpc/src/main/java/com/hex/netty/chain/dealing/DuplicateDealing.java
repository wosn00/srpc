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
 * <p>
 * 去重处理器(30s内)，集群部署模式需使用redis实现去重
 */
public class DuplicateDealing implements Dealing {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final Cache<Long, Boolean> DUPLICATE_CACHE = Caffeine.newBuilder()
            .expireAfterAccess(Duration.ofSeconds(30))
            .build();

    @Override
    public void deal(DealingContext context) {
        Command<String> command = context.getCommand();
        // 只有是请求才需要去重
        if (CommandType.REQUEST_COMMAND.getValue().equals(command.getCommandType())) {
            Long seq = command.getSeq();
            Boolean seqPresent = DUPLICATE_CACHE.getIfPresent(seq);
            boolean isDuplicated = false;
            if (seqPresent == null) {
                synchronized (DUPLICATE_CACHE) {
                    if (DUPLICATE_CACHE.getIfPresent(seq) == null) {
                        DUPLICATE_CACHE.put(seq, Boolean.TRUE);
                    } else {
                        isDuplicated = true;
                    }
                }
            } else {
                isDuplicated = true;
            }
            if (isDuplicated) {
                logger.warn("Received duplicate request seq=[{}], ignore it", seq);
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
