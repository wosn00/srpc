package com.hex.common.constant;

/**
 * @author guohs
 * @date 2021/7/15
 * 负载均衡策略
 */
public enum LoadBalanceRule {
    /**
     * 随机
     */
    RANDOM,

    /**
     * 轮询
     */
    ROUND,

    /**
     * 一致性hash
     */
    CONSISTENT_HASH,

    /**
     * 自定义负载均衡，需利用SPI机制
     */
    CUSTOM;
}
