package com.hex.rpc.sping.annotation;

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
@Impo
@Documented
public @interface EnableRpcClients {

    String[] basePackages() default {};
}
