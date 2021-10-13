package com.hex.srpc.core.rpc.compress.impl;

import com.hex.srpc.core.rpc.compress.Compress;
import org.anarres.lzo.LzoAlgorithm;
import org.anarres.lzo.LzoCompressor;
import org.anarres.lzo.LzoDecompressor;
import org.anarres.lzo.LzoInputStream;
import org.anarres.lzo.LzoLibrary;
import org.anarres.lzo.LzoOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author: hs
 */
public class LzoCompress implements Compress {

    @Override
    public byte[] compress(byte[] bytes) throws IOException {
        LzoCompressor compressor = LzoLibrary.getInstance().newCompressor(LzoAlgorithm.LZO1X, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try (LzoOutputStream cs = new LzoOutputStream(os, compressor);) {
            cs.write(bytes);
        }
        return os.toByteArray();
    }

    @Override
    public byte[] decompress(byte[] bytes) throws IOException {
        LzoDecompressor decompressor = LzoLibrary.getInstance().newDecompressor(LzoAlgorithm.LZO1X, null);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ByteArrayInputStream is = new ByteArrayInputStream(bytes);
             LzoInputStream us = new LzoInputStream(is, decompressor);
        ) {
            int count;
            byte[] buffer = new byte[2048];
            while ((count = us.read(buffer)) != -1) {
                baos.write(buffer, 0, count);
            }
            return baos.toByteArray();
        }
    }
}
