package com.hex.srpc.core.rpc.serialize;

import com.hex.common.annotation.SPI;

/**
 * @author: hs
 */
@SPI
public interface Serializer {

    /**
     * 序列化
     *
     * @param object 实体对象
     * @return 字节数组
     */
    byte[] serialize(Object object);

    /**
     * 反序列化
     *
     * @param bytes 序列化后的字节数组
     * @param clazz clazz
     * @param <T>   类型
     * @return 实体对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
