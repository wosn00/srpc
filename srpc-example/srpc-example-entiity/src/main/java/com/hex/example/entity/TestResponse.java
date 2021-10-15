package com.hex.example.entity;

import java.io.Serializable;

/**
 * @author: hs
 */
public class TestResponse implements Serializable {

    private static final long serialVersionUID = -7502021344405778255L;
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
