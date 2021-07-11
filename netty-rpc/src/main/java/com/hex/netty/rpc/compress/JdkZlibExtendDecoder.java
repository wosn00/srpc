package com.hex.netty.rpc.compress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.compression.JdkZlibDecoder;

import java.util.List;

/**
 * @author hs
 * <p>
 * isCompressed(1 byte) + compressData  ———>  rawData
 */
public class JdkZlibExtendDecoder extends JdkZlibDecoder {

    public JdkZlibExtendDecoder() {
    }

    public JdkZlibExtendDecoder(byte[] dictionary) {
        super(dictionary);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 1) {
            return;
        }
        boolean isZlibStream = in.readBoolean();
        if (!isZlibStream) {
            // 未压缩
            out.add(in.retainedDuplicate());
            in.skipBytes(in.readableBytes());
            return;
        }
        super.decode(ctx, in, out);
    }
}
