package com.hex.rpc.springboot.example.provider;

import com.hex.common.annotation.RouteBody;
import com.hex.common.annotation.RouteMapping;
import com.hex.common.annotation.SRpcRoute;
import com.hex.rpc.springboot.example.entity.TestRequest;
import com.hex.rpc.springboot.example.entity.TestResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author: hs
 * <p>
 * SprcRoute服务1
 * 标注了@SRpcRoute注解后类似@Service，可使用spring相关注解@Autowired等
 */
@SRpcRoute
public class RpcServerTestServiceImpl {

    @Autowired
    private TestService testService;

    @RouteMapping("test1")
    public String handler(@RouteBody String body) {

        System.out.println("test1收到请求内容：" + body);

        return "这是test1响应内容";
    }

    @RouteMapping("test2")
    public TestResponse handler2(@RouteBody TestRequest request) {

        System.out.println(testService.get());

        System.out.println("test2收到请求内容:" + request);

        return new TestResponse().setResponse("test2响应结果");
    }
}
