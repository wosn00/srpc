package com.hex.srpc.core.rpc.compress.impl;

import com.hex.srpc.core.rpc.compress.Compress;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author: hs
 * <p>
 * Bzip2压缩
 */
public class Bzip2Compress implements Compress {

    @Override
    public byte[] compress(byte[] bytes) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (BZip2CompressorOutputStream bcos = new BZip2CompressorOutputStream(out);) {
            bcos.write(bytes);
        }
        return out.toByteArray();
    }

    @Override
    public byte[] decompress(byte[] bytes) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             ByteArrayInputStream in = new ByteArrayInputStream(bytes);
             BZip2CompressorInputStream ungzip = new BZip2CompressorInputStream(in);
        ) {
            byte[] buffer = new byte[2048];
            int n;
            while ((n = ungzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        }
    }
}
