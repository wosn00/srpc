package com.hex.rpc.sping.processor;

import com.hex.netty.exception.RpcException;
import com.hex.netty.node.HostAndPort;
import com.hex.netty.rpc.Client;
import com.hex.netty.rpc.Server;
import com.hex.rpc.sping.registry.RpcServerAddressRegistry;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author: hs
 */
public class RpcPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RpcPostProcessor.class);

    private static Set<String> scanBasePackages = new HashSet<>();

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            if (bean instanceof Client) {
                List<HostAndPort> allNodes = RpcServerAddressRegistry.getAll();
                if (CollectionUtils.isEmpty(allNodes)) {
                    throw new RpcException("No rpc server node address was found");
                }
                ((Client) bean).contactNodes(allNodes).start();
            }
            if (bean instanceof Server) {
                ((Server) bean).configScanPackages(scanBasePackages).start();
            }
        } catch (RpcException e) {
            logger.error("Rpc postProcess failed", e);
        }
        return bean;
    }

    public static void registerBasePackages(Set<String> basePackages) {
        scanBasePackages.addAll(basePackages);
    }
}
