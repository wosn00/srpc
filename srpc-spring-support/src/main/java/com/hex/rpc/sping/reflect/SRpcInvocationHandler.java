package com.hex.rpc.sping.reflect;

import com.hex.common.annotation.RouteMapping;
import com.hex.common.net.HostAndPort;
import com.hex.rpc.sping.annotation.SRpcClient;
import com.hex.rpc.sping.registry.RpcServerAddressRegistry;
import com.hex.srpc.core.rpc.Client;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: hs
 */
public class SRpcInvocationHandler implements InvocationHandler {
    private static final Logger logger = LoggerFactory.getLogger(SRpcInvocationHandler.class);

    private Client client;
    private Class<?> type;
    private String typeName;
    private int timeoutRetryTimes;
    private Map<Method, RouterWrapper> methodCache = new HashMap<>(4);

    public SRpcInvocationHandler(ApplicationContext applicationContext, Class<?> type) {
        this.type = type;
        this.client = applicationContext.getBean(Client.class);
        resolveType();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        RouterWrapper routerWrapper = methodCache.get(method);
        if (routerWrapper != null) {
            String routerMapping = routerWrapper.getRouterMapping();
            Class<?> returnType = routerWrapper.getReturnType();
            String serviceName = RpcServerAddressRegistry.getServiceName(typeName);
            if (StringUtils.isNotBlank(serviceName)) {
                return client.invokeWithRegistry(routerMapping, args[0], returnType, serviceName, timeoutRetryTimes);
            }
            List<HostAndPort> hostAndPorts = RpcServerAddressRegistry.getHostAndPorts(typeName);
            return client.invoke(routerMapping, args[0], returnType, hostAndPorts, timeoutRetryTimes);
        }
        return null;
    }


    private void resolveType() {
        this.typeName = type.getCanonicalName();
        SRpcClient annotation = type.getAnnotation(SRpcClient.class);
        int retryTimes = annotation.retryTimes();
        if (retryTimes < 0 && logger.isWarnEnabled()) {
            logger.warn("Class {} annotation @SRpcClient with illegal retryTimes :{}, reset to 0",
                    typeName, retryTimes);
            retryTimes = 0;
        }
        timeoutRetryTimes = retryTimes;
        Method[] declaredMethods = type.getDeclaredMethods();
        for (Method method : declaredMethods) {
            if (method.isAnnotationPresent(RouteMapping.class)) {
                RouteMapping routeMapping = method.getAnnotation(RouteMapping.class);
                RouterWrapper wrapper = new RouterWrapper();
                String mapping = routeMapping.value();
                if (mapping.length() == 0) {
                    logger.error("Class {} Method {} does not have clearly routeMapping", typeName, method);
                    continue;
                }
                wrapper.setRouterMapping(mapping);
                wrapper.setReturnType(method.getReturnType());
                methodCache.put(method, wrapper);
            }
        }
        if (MapUtils.isEmpty(methodCache) && logger.isWarnEnabled()) {
            logger.warn("The method of the Class {} did not find any @RouteMapping annotation", typeName);
        }

    }

    static class RouterWrapper {
        /**
         * 映射路径
         */
        private String routerMapping;
        /**
         * 返回类型
         */
        private Class<?> returnType;

        String getRouterMapping() {
            return routerMapping;
        }

        RouterWrapper setRouterMapping(String routerMapping) {
            this.routerMapping = routerMapping;
            return this;
        }

        Class<?> getReturnType() {
            return returnType;
        }

        RouterWrapper setReturnType(Class<?> returnType) {
            this.returnType = returnType;
            return this;
        }
    }


}