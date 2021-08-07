package com.hex.entity;

/**
 * @author: hs
 */
public class TestResponse {

    private String response;


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
