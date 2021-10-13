package com.hex.common.utils;

import com.hex.common.exception.RpcException;

/**
 * @author: hs
 */
public class TypeUtil {

    public static <T> T convert(Object object, Class<T> clazz, String errorMsg) {
        if (clazz == null || clazz.isAssignableFrom(Void.TYPE)) {
            return null;
        }
        if (clazz.isInstance(object)) {
            return (T) object;
        } else {
            throw new RpcException(errorMsg);
        }
    }

}
