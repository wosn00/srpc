package com.hex.srpc.core.rpc.serialize.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hex.common.exception.SerializeException;
import com.hex.srpc.core.rpc.serialize.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author: hs
 * <p>
 * JSON序列化，使用fastjson
 */
public class JsonSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    @Override
    public byte[] serialize(Object object) {
        try {
            return JSON.toJSONString(object, SerializerFeature.WriteClassName).getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Json serialize failed", e);
            throw new SerializeException();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return JSON.parseObject(new String(bytes, StandardCharsets.UTF_8), clazz, Feature.SupportAutoType);
        } catch (Exception e) {
            logger.error("Json deserialize failed", e);
            throw new SerializeException();
        }
    }
}
