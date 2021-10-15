package com.hex.rpc.sping.annotation;

import com.hex.rpc.sping.registry.RpcComponentRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: hs
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(RpcComponentRegistrar.class)
@Documented
public @interface EnableSRpc {

    /**
     * 需扫描包路径, 需包含@SRpcRoute 和 @SRpcClient注解的类
     */
    String[] basePackages() default {};
}
