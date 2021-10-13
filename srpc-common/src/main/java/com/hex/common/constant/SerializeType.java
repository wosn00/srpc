package com.hex.common.constant;

/**
 * @author: hs
 */
public enum SerializeType {

    /**
     * 自定义序列化类型，需使用SPI
     */
    CUSTOM((byte) 0x1, "custom"),

    /**
     * protostuff序列化
     */
    PROTOSTUFF((byte) 0x5, "Protostuff"),

    /**
     * kyro序列化
     */
    KRYO((byte) 0x6, "Kryo"),

    /**
     * json序列化
     */
    JSON((byte) 0x7, "JSON"),

    /**
     * JDK序列化
     */
    JDK((byte) 0x8, "Jdk");

    private byte code;
    private String name;

    SerializeType(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public byte getCode() {
        return code;
    }

    public SerializeType setCode(byte code) {
        this.code = code;
        return this;
    }

    public String getName() {
        return name;
    }

    public SerializeType setName(String name) {
        this.name = name;
        return this;
    }

    public static String getName(int code) {
        for (SerializeType c : SerializeType.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }
}
