package com.hex.common.constant;

/**
 * @author: hs
 */
public interface RpcConstant {

    /**
     * 默认线程数 = cpu核数 * 2 +1
     */
    int DEFAULT_THREADS = Runtime.getRuntime().availableProcessors() * 2 + 1;

    String PING = "ping";

    String PONG = "pong";

    String SPI_CUSTOM_IMPL = "custom";

    short MAGIC_NUMBER = 0x1025;

    byte VERSION = 0x01;

    int COMPRESS_TYPE_LENGTH = 1;

    int SERIALIZE_TYPE_LENGTH = 1;

    int LENGTH_FIELD_LENGTH = 4;

    int MAX_FRAME_LENGTH = 10 * 1024 * 1024;
}
