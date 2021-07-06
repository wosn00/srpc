package com.hex.netty.compress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author hs
 * <p>
 * rawData  ———>  isCompressed(1 byte) + compressData
 */
public class JdkZlibExtendEncoder extends JdkZlibEncoder {
    private static final long NOT_LIMIT = -1L;

    private long minThreshold;

    private long maxThreshold;

    public JdkZlibExtendEncoder() {
        this(6, NOT_LIMIT, NOT_LIMIT);
    }

    public JdkZlibExtendEncoder(long minThreshold, long maxThreshold) {
        this(6, minThreshold, maxThreshold);
    }

    public JdkZlibExtendEncoder(int compressionLevel, long minThreshold, long maxThreshold) {
        super(compressionLevel);
        thresholdCheck(minThreshold, maxThreshold);
    }

    public JdkZlibExtendEncoder(int compressionLevel, byte[] dictionary, long minThreshold, long maxThreshold) {
        super(compressionLevel, dictionary);
        thresholdCheck(minThreshold, maxThreshold);
    }

    private void thresholdCheck(long minThreshold, long maxThreshold) {
        if (minThreshold < NOT_LIMIT || maxThreshold < NOT_LIMIT) {
            throw new IllegalArgumentException("minThreshold or maxThreshold is illegal, minThreshold:" +
                    minThreshold + ", maxThreshold:" + maxThreshold);
        }
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf uncompressed, ByteBuf out) throws Exception {

        if (this.minThreshold == NOT_LIMIT && this.maxThreshold == NOT_LIMIT) {
            compress(ctx, uncompressed, out);
            return;
        }
        int readableBytes = uncompressed.readableBytes();
        if (this.minThreshold == NOT_LIMIT) {
            if (readableBytes <= this.maxThreshold) {
                compress(ctx, uncompressed, out);
                return;
            }
        } else if (this.maxThreshold == NOT_LIMIT) {
            if (readableBytes >= minThreshold) {
                compress(ctx, uncompressed, out);
                return;
            }
        } else {
            if (this.minThreshold <= readableBytes && readableBytes <= this.maxThreshold) {
                compress(ctx, uncompressed, out);
                return;
            }
        }
        // 不压缩
        noCompress(uncompressed, out);

    }

    private void noCompress(ByteBuf in, ByteBuf out) {
        // 首字节表示数据未压缩
        out.writeBoolean(false);
        out.writeBytes(in);
    }

    private void compress(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
        // 首字节表示数据已压缩
        out.writeBoolean(true);
        super.encode(ctx, in, out);
    }

}