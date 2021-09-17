package com.hex.rpc.sping.annotation;

import com.hex.rpc.sping.registry.RpcClientRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: hs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RpcClientRegistrar.class)
@Documented
public @interface EnableSRpc {

    /**
     * 需扫描包路径
     */
    String[] basePackages() default {};
}
