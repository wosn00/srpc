package com.hex.netty.reflect;

import com.hex.netty.annotation.RouteMapping;
import com.hex.netty.annotation.RouteScan;
import com.hex.netty.annotation.RpcRoute;
import com.hex.netty.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: hs
 * 路由扫描器
 */
public class RouteScanner {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Class<?> primarySources;

    private Set<String> basePackages = new HashSet<>();

    private ClassLoader classLoader = RouteScanner.class.getClassLoader();

    public RouteScanner(Class<?> primarySources) {
        this.primarySources = primarySources;
    }

    public RouteScanner setBasePackages(Set<String> basePackages) {
        this.basePackages = basePackages;
        return this;
    }

    /**
     * 扫描并注册所有路由
     */
    public synchronized void san() {
        if (basePackages.isEmpty()) {
            if (primarySources == null) {
                throw new RpcException("please add an Class with annotation @RouteScan");
            }
            if (primarySources.isAnnotationPresent(RouteScan.class)) {
                RouteScan routeScan = primarySources.getDeclaredAnnotation(RouteScan.class);
                if (routeScan.value().length == 0) {
                    basePackages.add(primarySources.getPackage().getName());
                } else {
                    basePackages.addAll(Arrays.asList(routeScan.value()));
                }
            } else {
                logger.error("Class [{}] doesn't have @RouteScan annotation", primarySources.getSimpleName());
                return;
            }
        }
        for (String basePackage : basePackages) {
            basePackage = basePackage.replace(".", "/");
            URL resource = classLoader.getResource(basePackage);
            if (resource == null) {
                logger.warn("this package {} dose not exist", basePackage);
                continue;
            }
            File file = new File(resource.getFile());
            File[] files = file.listFiles();
            if (files == null) {
                logger.warn("the package {} does not have any class or package", basePackage);
                return;
            }
            loadAndRegister(files);
        }
        printScannedResult();
    }


    private void loadAndRegister(File[] files) {
        if (files == null) {
            return;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                loadAndRegister(file.listFiles());
                continue;
            }
            String classAbsolutePath = file.getAbsolutePath();
            String className = classAbsolutePath.substring(classAbsolutePath.indexOf("com"), classAbsolutePath.lastIndexOf(".class"));
            className = className.replace("\\", ".");
            Class<?> loadClass;
            try {
                loadClass = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                logger.error("Failed to load the scanned class", e);
                continue;
            }
            // 注册router
            registerRouter(loadClass);
        }
    }

    private void registerRouter(Class<?> clazz) {
        if (clazz.isAnnotationPresent(RpcRoute.class)) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(RouteMapping.class)) {
                    RouteMapping routeMapping = method.getDeclaredAnnotation(RouteMapping.class);
                    String route = routeMapping.value();
                    if (route.length() == 0) {
                        logger.warn("Class {} Method {} does not have clearly routeMapping, skip register", clazz, method);
                        continue;
                    }
                    // 注册进容器
                    RouterFactory.register(route, clazz, method);
                }
            }
        }
    }

    private void printScannedResult() {
        int mappingSize = RouterFactory.getMappingSize();
        if (mappingSize == 0) {
            logger.warn("No RouterMapping was scanned");
        } else {
            logger.info("[{}] RouterMapping was scanned", mappingSize);
        }
        if (logger.isDebugEnabled()) {
            Set<String> mappings = RouterFactory.getRouterTargetMap().keySet();
            logger.debug("Scanned RouterMapping: {}", mappings);
        }
    }
}
