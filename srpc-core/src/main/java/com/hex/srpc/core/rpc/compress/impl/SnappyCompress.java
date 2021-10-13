package com.hex.srpc.core.rpc.compress.impl;

import com.hex.srpc.core.rpc.compress.Compress;
import org.xerial.snappy.Snappy;

import java.io.IOException;

/**
 * @author: hs
 */
public class SnappyCompress implements Compress {

    @Override
    public byte[] compress(byte[] bytes) throws IOException {
        return Snappy.compress(bytes);
    }

    @Override
    public byte[] decompress(byte[] bytes) throws IOException {
        return Snappy.uncompress(bytes);
    }
}
