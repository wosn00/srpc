package com.hex.srpc.core.invoke;


import com.hex.common.cache.IExpireCache;
import com.hex.common.cache.ScheduleEvictExpireCache;

import java.util.concurrent.TimeUnit;

/**
 * @author: hs
 */
public class ResponseMapping {

    private IExpireCache<Long, ResponseFuture> expireCache;

    public ResponseMapping(int expiredTime) {
        expireCache = new ScheduleEvictExpireCache<>(expiredTime, TimeUnit.SECONDS);
    }

    public void putResponseFuture(Long requestSeq, ResponseFuture responseFuture) {
        expireCache.put(requestSeq, responseFuture);
    }

    public ResponseFuture getResponseFuture(Long requestId) {
        ResponseFuture responseFuture = expireCache.get(requestId);
        expireCache.invalidate(requestId);
        return responseFuture;
    }

    public void invalidate(Long requestId) {
        expireCache.invalidate(requestId);
    }
}
