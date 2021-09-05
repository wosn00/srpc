package com.hex.rpc.sping.registry;

import com.hex.srpc.core.node.HostAndPort;
import com.hex.rpc.sping.annotation.RpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author: hs
 */
public class RpcServerAddressRegistry {
    private static final Logger logger = LoggerFactory.getLogger(RpcServerAddressRegistry.class);

    private static final Map<String, List<HostAndPort>> CLASS_ADDRESS_MAP = new ConcurrentHashMap<>();

    public static void register(AnnotationMetadata annotationMetadata) {
        Class<?> clazz;
        try {
            clazz = Class.forName(annotationMetadata.getClassName());
        } catch (ClassNotFoundException e) {
            logger.error("load client Class failed", e);
            return;
        }
        if (!clazz.isAnnotationPresent(RpcClient.class)) {
            logger.error("Class {} no @RpcClient annotation, register rpc server address failed", clazz);
            return;
        }
        RpcClient annotation = clazz.getAnnotation(RpcClient.class);
        String[] servers = annotation.servers();
        if (servers.length == 0) {
            logger.error("annotation @RpcClient must define attribute servers, the server address must be specified");
        }
        List<HostAndPort> hostAndPorts = CLASS_ADDRESS_MAP.computeIfAbsent(clazz.getCanonicalName(), k -> new ArrayList<>());
        for (String server : servers) {
            hostAndPorts.add(HostAndPort.from(server));
        }
    }

    public static List<HostAndPort> getHostAndPorts(String className) {
        return CLASS_ADDRESS_MAP.get(className);
    }

    public static List<HostAndPort> getAll() {
        return CLASS_ADDRESS_MAP.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

}
