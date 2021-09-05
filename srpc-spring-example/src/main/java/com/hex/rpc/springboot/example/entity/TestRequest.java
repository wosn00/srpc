package com.hex.rpc.springboot.example.entity;

/**
 * @author: hs
 */
public class TestRequest {

    private String name;
    private String body;

    public String getName() {
        return name;
    }

    public TestRequest setName(String name) {
        this.name = name;
        return this;
    }

    public String getBody() {
        return body;
    }

    public TestRequest setBody(String body) {
        this.body = body;
        return this;
    }

    @Override
    public String toString() {
        return "TestRequest{" +
                "name='" + name + '\'' +
                ", body='" + body + '\'' +
                '}';
    }
}
