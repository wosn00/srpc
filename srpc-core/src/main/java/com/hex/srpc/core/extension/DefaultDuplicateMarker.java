package com.hex.srpc.core.extension;

import com.hex.common.cache.IExpireCache;
import com.hex.common.cache.ScheduleEvictExpireCache;

import java.util.concurrent.TimeUnit;

/**
 * @author: hs
 * 内存级别去重处理，集群模式下需使用SPI自定义redis实现
 */
public class DefaultDuplicateMarker implements DuplicatedMarker {

    private IExpireCache<Long, Boolean> expireCache;

    @Override
    public void initMarkerConfig(int expireTime, long maxSize) {
        expireCache = new ScheduleEvictExpireCache<>(expireTime, TimeUnit.SECONDS, maxSize);
    }

    @Override
    public boolean mark(Long seq) {
        return expireCache.put(seq, Boolean.TRUE) != null;
    }
}
