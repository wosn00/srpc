package com.hex.netty.reflection;

import com.google.common.base.Throwables;
import com.hex.netty.annotation.RouteBody;
import com.hex.netty.protocol.Command;
import com.hex.netty.utils.SerializerUtil;
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

    private Integer paramAnnotationPos;

    private Object[] args;

    private Type paramAnnotationType;

    public RouterTarget(Object router, Method method) {
        this.router = router;
        this.method = method;
        Parameter[] parameters = method.getParameters();
        args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(RouteBody.class)) {
                paramAnnotationPos = i;
                paramAnnotationType = parameters[i].getAnnotatedType().getType();
                break;
            }
        }
    }

    public String invoke(Command<String> command) {
        if (paramAnnotationPos != null) {
            args[paramAnnotationPos] = SerializerUtil.deserialize(command.getBody(), paramAnnotationType);
        }

        Object result = null;
        try {
            result = method.invoke(router, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            logger.error(Throwables.getStackTraceAsString(e));
        }
        return SerializerUtil.serialize(result);
    }
}
