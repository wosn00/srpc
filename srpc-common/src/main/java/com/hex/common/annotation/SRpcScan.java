package com.hex.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: hs
 * 路由扫描
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SRpcScan {
    /**
     * 自定义需要扫描的包, 默认为当前类所在的包名
     */
    String[] value() default {};
}
