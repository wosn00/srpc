package com.hex.netty.chain.dealing;

import com.hex.netty.chain.Dealing;
import com.hex.netty.chain.DealingContext;

/**
 * @author: hs
 * 去重处理器
 */
public class DuplicateDealing implements Dealing {
    @Override
    public void deal(DealingContext context) {


        context.nextDealing();
    }
}
