package com.hex.srpc.core.rpc.compress.impl;

import com.hex.srpc.core.rpc.compress.Compress;
import net.jpountz.lz4.LZ4BlockInputStream;
import net.jpountz.lz4.LZ4BlockOutputStream;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Factory;
import net.jpountz.lz4.LZ4FastDecompressor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author: hs
 * <p>
 * LZ4压缩
 */
public class LZ4Compress implements Compress {

    @Override
    public byte[] compress(byte[] bytes) throws IOException {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
        LZ4Compressor compressor = factory.fastCompressor();
        try (LZ4BlockOutputStream compressedOutput = new LZ4BlockOutputStream(byteOutput, 2048, compressor)) {
            compressedOutput.write(bytes);
        }
        return byteOutput.toByteArray();
    }

    @Override
    public byte[] decompress(byte[] bytes) throws IOException {
        LZ4Factory factory = LZ4Factory.fastestInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        LZ4FastDecompressor decompresser = factory.fastDecompressor();

        try (LZ4BlockInputStream lzis = new LZ4BlockInputStream(new ByteArrayInputStream(bytes), decompresser);) {
            int count;
            byte[] buffer = new byte[2048];
            while ((count = lzis.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }
        }

        return baos.toByteArray();
    }
}
