package com.hex.rpc.spring.starter.config;

import com.hex.netty.config.RpcClientConfig;
import com.hex.netty.config.RpcServerConfig;
import com.hex.netty.rpc.Client;
import com.hex.netty.rpc.Server;
import com.hex.netty.rpc.client.SrpcClient;
import com.hex.netty.rpc.server.SrpcServer;
import com.hex.rpc.spring.starter.properties.RpcClientProperties;
import com.hex.rpc.spring.starter.properties.RpcServerProperties;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author: hs
 */
@Configuration
@Import({RpcClientProperties.class, RpcServerProperties.class})
public class RpcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Server.class)
    public Server rpcServer(RpcServerProperties rpcServerProperties) {
        RpcServerConfig config = new RpcServerConfig();
        BeanUtils.copyProperties(rpcServerProperties, config);
        return SrpcServer.builder()
                .config(config);
    }


    @Bean
    @ConditionalOnMissingBean(Client.class)
    public Client rpcClient(RpcClientProperties rpcClientProperties) {
        RpcClientConfig config = new RpcClientConfig();
        BeanUtils.copyProperties(rpcClientProperties, config);
        return SrpcClient.builder()
                .config(config);
    }

}
