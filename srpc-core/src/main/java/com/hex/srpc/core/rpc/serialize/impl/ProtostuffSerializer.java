package com.hex.srpc.core.rpc.serialize.impl;

import com.hex.srpc.core.rpc.serialize.Serializer;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: hs
 * <p>
 * Protostuff序列化
 */
public class ProtostuffSerializer implements Serializer {

    /**
     * 缓存Schema
     */
    private static Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    @Override
    public byte[] serialize(Object object) {
        LinkedBuffer buffer = LinkedBuffer.allocate();
        try {
            Schema schema = getSchema(object.getClass());
            return ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        Schema<T> schema = (Schema<T>) getSchema(clazz);
        T obj = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);
        return obj;
    }

    private static Schema<?> getSchema(Class<?> clazz) {
        return schemaCache.computeIfAbsent(clazz, k -> RuntimeSchema.getSchema(clazz));
    }
}
