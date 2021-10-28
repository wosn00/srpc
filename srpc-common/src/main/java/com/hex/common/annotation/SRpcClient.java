package com.hex.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: hs
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SRpcClient {

    /**
     * 指定rpc服务节点地址
     * 配置方式一 ：以分号隔开地址，example: @SRpcClient(nodes = "127.0.0.1:9955;127.0.0.1:9956")
     * 配置方式二 ：支持${}配置方式从yml或properties中读取配置，example: @SRpcClient(nodes = "${rpc.server}")
     */
    String nodes() default "";

    /**
     * rpc注册中心服务名称
     */
    String serviceName() default "";

    /**
     * 请求超时重试次数
     */
    int retryTimes() default 0;
}
