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

import java.util.List;

/**
 * @author: hs
 */
public class RpcPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RpcPostProcessor.class);

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Client) {
            List<HostAndPort> allNodes = RpcServerAddressRegistry.getAll();
            if (CollectionUtils.isEmpty(allNodes)) {
                throw new RpcException("No rpc service node address was found");
            }
            return ((Client) bean).contactNodes(allNodes).start();
        }
        if (bean instanceof Server) {

        }

        return bean;
    }
}
