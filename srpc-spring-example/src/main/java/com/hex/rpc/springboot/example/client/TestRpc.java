package com.hex.rpc.springboot.example.client;

import com.hex.rpc.springboot.example.entity.TestRequest;
import com.hex.rpc.springboot.example.entity.TestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author: hs
 * <p>
 * 测试rpc调用
 */
@Component
public class TestRpc implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private RpcServerTestService testService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        String result = testService.handler("测试发送");
        System.out.println(result);

        TestRequest request = new TestRequest().setName("hs").setBody("测试请求");

        for (int i = 0; i < 20; i++) {
            // 同步发送请求，获取响应
            TestResponse response = testService.handler2(request);
            System.out.println("这是第" + i + "个响应内容:" + response);
        }
    }
}
