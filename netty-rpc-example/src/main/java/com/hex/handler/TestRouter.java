package com.hex.handler;

import com.hex.entity.WeatherData;
import com.hex.netty.annotation.RouteBody;
import com.hex.netty.annotation.RouteMapping;
import com.hex.netty.annotation.RpcRoute;

/**
 * @author: hs
 */
@RpcRoute
public class TestRouter {

    @RouteMapping("/myRouter/test")
    public String handler(@RouteBody String body) {
        System.out.println("test1收到请求内容：" + body);
        return "这是test1响应内容";
    }

    @RouteMapping("/myRouter/getWeather")
    public WeatherData handler2(@RouteBody String address) {
        WeatherData data = new WeatherData();
        data.setAddress(address);
        data.setTemperature(41);
        data.setWeather("sunny");
        System.out.println("getWeather收到请求内容：" + address);
        return data;
    }
}
