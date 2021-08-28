package com.hex.rpc.sping.reflect;

import com.hex.netty.annotation.RouteMapping;
import com.hex.netty.exception.RpcException;
import com.hex.netty.node.HostAndPort;
import com.hex.netty.rpc.Client;
import com.hex.rpc.sping.registry.RpcServerAddressRegistry;
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
public class RpcInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(RpcInvocationHandler.class);

    private ApplicationContext applicationContext;

    private volatile Client client;

    private Class<?> type;

    public RpcInvocationHandler(ApplicationContext applicationContext, Class<?> type) {
        this.applicationContext = applicationContext;
        this.type = type;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isAnnotationPresent(RouteMapping.class)) {
            getClientBean();
            RouteMapping routeMapping = method.getDeclaredAnnotation(RouteMapping.class);
            String route = routeMapping.value();
            if (route.length() == 0) {
                logger.error("Class {} Method {} does not have clearly routeMapping", proxy.getClass(), method);
                return null;
            }
            Class<?> returnType = method.getReturnType();
            List<HostAndPort> hostAndPorts = RpcServerAddressRegistry.getHostAndPorts(type.getCanonicalName());
            return client.invoke(route, args[0], returnType, hostAndPorts);
        }
        return null;
    }

    private void getClientBean() {
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