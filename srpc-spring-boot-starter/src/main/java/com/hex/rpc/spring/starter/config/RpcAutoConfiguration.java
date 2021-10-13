package com.hex.rpc.spring.starter.config;

import com.hex.rpc.spring.starter.properties.RpcClientProperties;
import com.hex.rpc.spring.starter.properties.RpcServerProperties;
import com.hex.srpc.core.config.RegistryConfig;
import com.hex.srpc.core.config.SRpcClientConfig;
import com.hex.srpc.core.config.SRpcServerConfig;
import com.hex.srpc.core.rpc.Client;
import com.hex.srpc.core.rpc.Server;
import com.hex.srpc.core.rpc.client.SRpcClient;
import com.hex.srpc.core.rpc.server.SRpcServer;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * @author: hs
 */
@Configuration
@Import({RpcClientProperties.class, RpcServerProperties.class})
public class RpcAutoConfiguration {

    private static final String APPLICATION_NAME_KEY = "spring.application.name";

    @Bean
    @ConditionalOnMissingBean(Server.class)
    public Server rpcServer(RpcServerProperties rpcServerProperties, Environment environment) {
        String serviceName = environment.getProperty(APPLICATION_NAME_KEY);
        SRpcServerConfig config = new SRpcServerConfig();
        RegistryConfig registryConfig = new RegistryConfig();
        BeanUtils.copyProperties(rpcServerProperties, config);
        BeanUtils.copyProperties(rpcServerProperties, registryConfig);

        Server server = SRpcServer.builder()
                .serverConfig(config)
                .sourceClass(Void.class);
        if (registryConfig.isEnableRegistry()) {
            server.configRegistry(registryConfig.getRegistrySchema(), registryConfig.getRegistryAddress(), serviceName);
        }
        return server;
    }

    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client rpcClient(RpcClientProperties rpcClientProperties) {
        SRpcClientConfig config = new SRpcClientConfig();
        RegistryConfig registryConfig = new RegistryConfig();
        BeanUtils.copyProperties(rpcClientProperties, config);
        BeanUtils.copyProperties(rpcClientProperties, registryConfig);

        Client client = SRpcClient.builder()
                .config(config);
        if (registryConfig.isEnableRegistry()) {
            client.configRegistry(registryConfig.getRegistrySchema(), registryConfig.getRegistryAddress());
        }
        return client;
    }

}
