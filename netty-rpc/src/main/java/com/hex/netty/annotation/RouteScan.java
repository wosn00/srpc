package com.hex.netty.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: hs
 * 路由扫描，如果不填包名的话默认为当前类所在的包名
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RouteScan {
    /**
     * 自定义需要扫描的包
     */
    String[] value() default {};
}
