package com.hex.rpc.springboot.example;

import com.hex.rpc.sping.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class RpcSpringBootExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(RpcSpringBootExampleApplication.class, args);
    }

}
