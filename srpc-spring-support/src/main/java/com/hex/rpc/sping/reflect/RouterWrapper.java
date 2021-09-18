package com.hex.rpc.sping.reflect;

/**
 * @author guohs
 * @date 2021/9/18
 */
public class RouterWrapper {

    /**
     * 映射路径
     */
    private String routerMapping;

    /**
     * 返回类型
     */
    private Class<?> returnType;

    public String getRouterMapping() {
        return routerMapping;
    }

    public RouterWrapper setRouterMapping(String routerMapping) {
        this.routerMapping = routerMapping;
        return this;
    }

    public Class<?> getReturnType() {
        return returnType;
    }

    public RouterWrapper setReturnType(Class<?> returnType) {
        this.returnType = returnType;
        return this;
    }
}
