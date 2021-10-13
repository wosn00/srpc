package com.hex.srpc.core.rpc.serialize.impl;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoFactory;
import com.esotericsoftware.kryo.pool.KryoPool;
import com.hex.common.exception.SerializeException;
import com.hex.srpc.core.rpc.serialize.Serializer;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @author: hs
 * <p>
 * Kryo序列化
 */
public class KryoSerializer implements Serializer {
    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);
    /**
     * （池化Kryo实例）使用ThreadLocal
     */
    private static final ThreadLocal<Kryo> kryos = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        //支持对象循环引用（否则会栈溢出）
        kryo.setReferences(true); //默认值就是 true，添加此行的目的是为了提醒维护者，不要改变这个配置
        //不强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
        kryo.setRegistrationRequired(false); //默认值就是 false，添加此行的目的是为了提醒维护者，不要改变这个配置
        //Fix the NPE bug when deserializing Collections.
        ((Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy()).setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
        return kryo;
    });
    /**
     * （池化Kryo实例）使用KryoPool
     */
    private static KryoFactory factory = Kryo::new;
    private static KryoPool pool = new KryoPool.Builder(factory).softReferences().build();


    /**
     * 使用ThreadLocal创建Kryo
     * 把java对象序列化成byte[];
     *
     * @param obj java对象
     * @return 序列化后的字节数组
     */
    @Override
    public byte[] serialize(Object obj) {
        if (null != obj) {
            Kryo kryo = kryos.get();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try (Output output = new Output(os);) {
                kryo.writeObject(output, obj);
            } catch (Exception e) {
                logger.error("Kryo serialize failed", e);
                throw new SerializeException();
            }
            return os.toByteArray();
        }
        return new byte[0];
    }

    /**
     * 使用ThreadLocal创建Kryo
     * 把byte[]反序列化成指定的java对象
     *
     * @param bytes 序列化后的字节数组
     * @param clazz 指定的java对象
     * @return 指定的java对象
     */
    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        if (null != bytes && bytes.length > 0 && null != clazz) {
            Kryo kryo = kryos.get();
            ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            try (Input input = new Input(is);) {
                return kryo.readObject(input, clazz);
            } catch (Exception e) {
                logger.error("Kryo deserialize failed", e);
                throw new SerializeException();
            }
        }
        return null;
    }
}
