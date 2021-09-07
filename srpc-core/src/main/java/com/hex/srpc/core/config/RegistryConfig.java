package com.hex.srpc.core.config;

import java.util.List;

/**
 * @author: hs
 */
public class RegistryConfig {
    public static final String DEFAULT_REGISTRY_SCHEMA = "zookeeper";

    private boolean enableRegistry;
    private String registrySchema;
    private List<String> registryAddress;

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
}
