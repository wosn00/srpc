package com.hex.common.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hex.common.exception.SerializeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

/**
 * @author: hs
 */
public class SerializerUtil {
    private static final Logger logger = LoggerFactory.getLogger(SerializerUtil.class);

    public static String serialize(Object o) {
        try {
            return JSON.toJSONString(o);
        } catch (Exception e) {
            logger.error("JsonSerialize failed", e);
            throw new SerializeException();
        }
    }

    public static String serializePretty(Object o) {
        try {
            return JSON.toJSONString(o, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.PrettyFormat);
        } catch (Exception e) {
            logger.error("JsonSerialize failed", e);
            throw new SerializeException();
        }
    }

    public static <E> E deserialize(String body, Class<E> clazz) {
        try {
            return JSON.parseObject(body, clazz);
        } catch (Exception e) {
            logger.error("JsonDeserialize failed", e);
            throw new SerializeException();
        }
    }

    public static <E> E deserialize(String body, Type type) {
        try {
            return JSON.parseObject(body, type);
        } catch (Exception e) {
            logger.error("JsonDeserialize failed", e);
            throw new SerializeException();
        }
    }
}
