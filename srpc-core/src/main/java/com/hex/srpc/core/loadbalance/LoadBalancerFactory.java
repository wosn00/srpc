package com.hex.srpc.core.loadbalance;

import com.hex.common.constant.LoadBalanceRule;
import com.hex.common.spi.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author guohs
 * @date 2021/7/15
 */
public class LoadBalancerFactory {
    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerFactory.class);

    public static LoadBalancer getLoadBalance(LoadBalanceRule rule) {
        ExtensionLoader<LoadBalancer> extensionLoader =
                ExtensionLoader.getExtensionLoader(LoadBalancer.class);
        LoadBalancer loadBalancer = extensionLoader.getExtension(rule.name());
        if (logger.isDebugEnabled()) {
            logger.debug("Use the {} LoadBalancer, Class {}", rule, loadBalancer.getClass());
        }
        return loadBalancer;
    }
}
