package com.hex.rpc.sping.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: hs
 * <p>
 * example:
 * @RpcClient({"ip:port","ip:port"})
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SRpcClient {

    /**
     * rpc服务节点地址
     */
    String[] nodes() default {};

    /**
     * rpc注册中心服务名称
     */
    String serviceName() default "";

    /**
     * 请求超时重试次数
     */
    int retryTimes() default 0;
}
