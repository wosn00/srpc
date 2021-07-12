package com.hex.netty.reflection;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.hex.netty.annotation.RouteBody;
import com.hex.netty.protocol.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * @author: hs
 */
public class RouterTarget {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private Object router;

    private Method method;

    public RouterTarget(Object router, Method method) {
        this.router = router;
        this.method = method;
    }

    public String invoke(Command<String> command) {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RouteBody.class)) {
                Type type = parameters[i].getAnnotatedType().getType();
                args[i] = JSON.parseObject(command.getBody(), type);
            }
        }
        Object result = null;
        try {
            result = method.invoke(router, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(Throwables.getStackTraceAsString(e));
        }
        return JSON.toJSONString(result);
    }

}
