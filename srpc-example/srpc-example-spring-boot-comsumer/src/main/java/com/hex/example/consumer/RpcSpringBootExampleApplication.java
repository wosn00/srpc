package com.hex.example.consumer;

import com.hex.rpc.sping.annotation.EnableSRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableSRpc(basePackages = "com.hex.example")
public class RpcSpringBootExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcSpringBootExampleApplication.class, args);
    }

}
