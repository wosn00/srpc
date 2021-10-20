package com.hex.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author guohs
 * @date 2021/10/19
 */
public class MappingUtil {
    private static final Logger logger = LoggerFactory.getLogger(MappingUtil.class);

    public static String generateMapping(Class<?> type, Method method) {
        StringBuilder sb = new StringBuilder();
        sb.append(type.getSimpleName());
        sb.append(method.getName());
        for (Class<?> parameterType : method.getParameterTypes()) {
            sb.append(parameterType.getCanonicalName());
        }
        return getMd5Instance(sb.toString());
    }

    public static String getMd5Instance(String str) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(str.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}
