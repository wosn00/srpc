package com.hex.rpc.sping.reflect;

import com.hex.common.annotation.RouteMapping;
import com.hex.common.exception.RpcException;
import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.rpc.Client;
import com.hex.rpc.sping.registry.RpcServerAddressRegistry;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author: hs
 */
public class SRpcInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(SRpcInvocationHandler.class);

    private ApplicationContext applicationContext;

    private volatile Client client;

    private Class<?> type;

    public SRpcInvocationHandler(ApplicationContext applicationContext, Class<?> type) {
        this.applicationContext = applicationContext;
        this.type = type;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.isAnnotationPresent(RouteMapping.class)) {
            getSRpcClientBean();
            RouteMapping routeMapping = method.getDeclaredAnnotation(RouteMapping.class);
            String route = routeMapping.value();
            if (route.length() == 0) {
                logger.error("Class {} Method {} does not have clearly routeMapping", proxy.getClass(), method);
                return null;
            }
            Class<?> returnType = method.getReturnType();
            String canonicalName = type.getCanonicalName();
            String serviceName = RpcServerAddressRegistry.getServiceName(canonicalName);
            if (StringUtils.isNotBlank(serviceName)) {
                return client.invokeWithRegistry(route, args[0], returnType, serviceName);
            }
            List<HostAndPort> hostAndPorts = RpcServerAddressRegistry.getHostAndPorts(canonicalName);
            return client.invoke(route, args[0], returnType, hostAndPorts);
        }
        return null;
    }

    private void getSRpcClientBean() {
        if (client == null) {
            synchronized (this) {
                if (client == null) {
                    try {
                        client = applicationContext.getBean(Client.class);
                    } catch (NoSuchBeanDefinitionException e) {
                        throw new RpcException("no bean of RpcClient be found");
                    }
                }
            }
        }
    }

}