package com.hex.rpc.springboot.example.server;

import org.springframework.stereotype.Component;

/**
 * @author: hs
 */
@Component
public class TestService {

    public String get() {
        return "this is from spring";
    }
}
