package com.hex.srpc.core.rpc.compress;

import com.hex.common.annotation.SPI;

import java.io.IOException;

/**
 * @author: hs
 */
@SPI
public interface Compress {

    /**
     * 压缩
     *
     * @param bytes 原始字节数组
     * @return 压缩后的字节数组
     */
    byte[] compress(byte[] bytes) throws IOException;

    /**
     * 解压
     *
     * @param bytes 压缩后的字节数组
     * @return 原始字节数组
     */
    byte[] decompress(byte[] bytes) throws IOException;

}
