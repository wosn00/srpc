package com.hex.netty.invoke;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.time.Duration;

/**
 * @author: hs
 */
public class ResponseMapping {
    /**
     * 响应最长等待时间2Min
     */
    private static Cache<String, ResponseFuture> mapping = CacheBuilder.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(30))
            .build();

    public static void putResponseFuture(String requestId, ResponseFuture responseFuture) {
        mapping.put(requestId, responseFuture);
    }

    public static ResponseFuture getResponseFuture(String requestId) {
        return mapping.getIfPresent(requestId);
    }


}
