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

    private static Map<String, RouterMapping> routerMappingMap = new ConcurrentHashMap<>();

    private static Map<String, Object> factory = new ConcurrentHashMap<>();

    private RouterFactory() {
    }

    /**
     * 注册Router，单例模式
     */
    public static synchronized void register(String mapping, Class<?> clazz, Method method) {
        Object o = factory.computeIfAbsent(clazz.getName(), key -> {
            try {
                return clazz.getConstructor().newInstance();
            } catch (Exception e) {
                logger.error("Router [{}] instantiation failed", clazz.getName(), e);
                return null;
            }
        });
        RouterMapping routerMapping = new RouterMapping(mapping, o, method);
        routerMappingMap.put(mapping, routerMapping);
    }

    public static RouterMapping getRouter(String mapping) {
        return routerMappingMap.get(mapping);
    }

    public static int getMappingSize() {
        return routerMappingMap.size();
    }
}
