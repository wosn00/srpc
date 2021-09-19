package com.hex.srpc.core.config;

import java.util.List;

/**
 * @author: hs
 */
public class RegistryConfig {
    public static final String DEFAULT_REGISTRY_SCHEMA = "zookeeper";

    /**
     * 是否使用注册中心
     */
    private boolean enableRegistry;

    /**
     * 注册中心模式，缺省为zookeeper，其他注册中心可基于spi机制自行扩展实现
     */
    private String registrySchema;

    /**
     * 注册中心地址
     */
    private List<String> registryAddress;

    /**
     * 需要发布到注册中心上的服务名称[服务提供端需要配置]
     */
    private String serviceName;

    public boolean isEnableRegistry() {
        return enableRegistry;
    }

    public RegistryConfig setEnableRegistry(boolean enableRegistry) {
        this.enableRegistry = enableRegistry;
        return this;
    }

    public String getRegistrySchema() {
        return registrySchema;
    }

    public RegistryConfig setRegistrySchema(String registrySchema) {
        this.registrySchema = registrySchema;
        return this;
    }

    public List<String> getRegistryAddress() {
        return registryAddress;
    }

    public RegistryConfig setRegistryAddress(List<String> registryAddress) {
        this.registryAddress = registryAddress;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public RegistryConfig setServiceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }
}
