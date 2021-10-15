package com.hex.example.provider.service;

import org.springframework.stereotype.Component;

/**
 * @author: hs
 */
@Component
public class TestService {

    public String get() {
        return "this is a bean from spring";
    }
}
