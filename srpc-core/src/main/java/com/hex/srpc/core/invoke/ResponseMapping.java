package com.hex.srpc.core.invoke;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;

/**
 * @author: hs
 */
public class ResponseMapping {
    /**
     * 响应最长等待时间30s
     */
    private static Cache<Long, ResponseFuture> futureCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(30))
            .build();

    private ResponseMapping() {

    }

    public static void putResponseFuture(Long requestSeq, ResponseFuture responseFuture) {
        futureCache.put(requestSeq, responseFuture);
    }

    public static ResponseFuture getResponseFuture(Long requestId) {
        ResponseFuture responseFuture = futureCache.getIfPresent(requestId);
        futureCache.invalidate(requestId);
        return responseFuture;
    }

    public static void invalidate(Long requestId) {
        futureCache.invalidate(requestId);
    }
}
