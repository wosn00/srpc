package com.hex.netty.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: hs
 * Router工厂
 */
public class RouterFactory {
    private static Logger logger = LoggerFactory.getLogger(RouterFactory.class);

    private static Map<String, RouterTarget> routerTargetMap = new ConcurrentHashMap<>();

    private static Map<String, Object> factory = new ConcurrentHashMap<>();

    private RouterFactory() {
    }

    /**
     * 注册Router，单例模式
     */
    public static synchronized void register(String mapping, Class<?> clazz, Method method) {
        Object instance = factory.computeIfAbsent(clazz.getName(), key -> {
            try {
                return clazz.getConstructor().newInstance();
            } catch (Exception e) {
                logger.error("Router [{}] instantiation failed", clazz.getName(), e);
                return null;
            }
        });
        RouterTarget routerTarget = new RouterTarget(instance, method);
        routerTargetMap.put(mapping, routerTarget);
    }

    public static RouterTarget getRouter(String mapping) {
        return routerTargetMap.get(mapping);
    }

    public static int getMappingSize() {
        return routerTargetMap.size();
    }

    public static Map<String, RouterTarget> getRouterTargetMap() {
        return routerTargetMap;
    }
}
