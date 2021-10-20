package com.hex.srpc.core.reflect;

import com.hex.common.annotation.Mapping;
import com.hex.common.annotation.SRpcClient;
import com.hex.common.exception.RpcException;
import com.hex.common.utils.MappingUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author: hs
 * Router工厂
 */
public class RouterFactory {
    private static Logger logger = LoggerFactory.getLogger(RouterFactory.class);

    private static Set<Class<?>> routerClasses = new CopyOnWriteArraySet<>();

    private static Map<Class<?>, Object> factory = new ConcurrentHashMap<>();

    private static Map<String, RouterTarget> routerTargetMap = new ConcurrentHashMap<>();


    private RouterFactory() {
    }

    /**
     * 注册Router，单例模式
     * <p>
     * factory中若不存在route对应的实例，则自行实例化
     */
    public static synchronized void register(Class<?> clazz) {
        // 注册进容器
        Object instance = factory.computeIfAbsent(clazz, key -> {
            try {
                return clazz.getConstructor().newInstance();
            } catch (Exception e) {
                logger.error("Router {} instantiation failed", clazz.getName(), e);
                return null;
            }
        });
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            String mapping = null;
            if (method.isAnnotationPresent(Mapping.class)) {
                Mapping annotation = method.getDeclaredAnnotation(Mapping.class);
                mapping = annotation.value();
                if (mapping.length() == 0) {
                    throw new RpcException("Class " + clazz + " Method " + method + " does not have clearly Mapping, skip register");
                }
            } else {
                for (Class<?> anInterface : clazz.getInterfaces()) {
                    if (anInterface.isAnnotationPresent(SRpcClient.class)) {
                        //默认生成唯一标识
                        mapping = MappingUtil.generateMapping(anInterface, method);
                        break;
                    }
                }
            }
            if (mapping == null) {
                throw new RpcException("class" + clazz.getCanonicalName() + "must implement an interface annotated @SRpcClient");
            }
            RouterTarget routerTarget = new RouterTarget(instance, method);
            routerTargetMap.put(mapping, routerTarget);
        }
    }

    public static RouterTarget getRouter(String mapping) {
        RouterTarget routerTarget = routerTargetMap.get(mapping);
        if (routerTarget == null) {
            throw new RpcException("RouterTarget not found, ,Mapping: " + mapping);
        }
        return routerTarget;
    }

    public static int getMappingSize() {
        return routerTargetMap.size();
    }

    public static Map<String, RouterTarget> getRouterTargetMap() {
        return routerTargetMap;
    }

    public static void addRouterClazz(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error("add route Class failed", e);
            return;
        }
        routerClasses.add(clazz);
    }

    public static Set<Class<?>> getAllRouteClasses() {
        return routerClasses;
    }

    public static void addRouteInstance(Class<?> clazz, Object route) {
        factory.put(clazz, route);
    }

}
