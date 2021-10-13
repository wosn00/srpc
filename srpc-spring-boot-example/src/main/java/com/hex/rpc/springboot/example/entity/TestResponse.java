package com.hex.rpc.springboot.example.entity;

/**
 * @author: hs
 */
public class TestResponse {

    private String response;

    public TestResponse() {
    }

    public String getResponse() {
        return response;
    }

    public TestResponse setResponse(String response) {
        this.response = response;
        return this;
    }

    @Override
    public String toString() {
        return "TestResponse{" +
                "response='" + response + '\'' +
                '}';
    }
}
