package com.hex.netty.invoke;


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
    private static Cache<String, ResponseFuture> futureCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(30))
            .build();

    public static void putResponseFuture(String requestId, ResponseFuture responseFuture) {
        futureCache.put(requestId, responseFuture);
    }

    public static ResponseFuture getResponseFuture(String requestId) {
        ResponseFuture responseFuture = futureCache.getIfPresent(requestId);
        futureCache.invalidate(requestId);
        return responseFuture;
    }
}
