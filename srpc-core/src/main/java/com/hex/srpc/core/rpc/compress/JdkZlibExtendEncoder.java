package com.hex.srpc.core.rpc.compress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author hs
 * <p>
 * 扩展JdkZlibEncoder，实现可自定义大小范围内进行包压缩
 * <p>
 * rawData  ———>  isCompressed(1 byte) + compressData
 */
public class JdkZlibExtendEncoder extends JdkZlibEncoder {
    private static final long NOT_LIMIT = -1L;

    /**
     * 是否开启压缩
     */
    private boolean enableCompress;

    /**
     * 压缩最小阈值,包大于最小阈值则进行压缩
     */
    private long minThreshold;

    /**
     * 压缩最大阈值,包小于最大阈值则进行压缩
     */
    private long maxThreshold;

    public JdkZlibExtendEncoder(boolean enableCompress) {
        this(enableCompress, 6, NOT_LIMIT, NOT_LIMIT);
    }

    public JdkZlibExtendEncoder(boolean enableCompress, long minThreshold, long maxThreshold) {
        this(enableCompress, 6, minThreshold, maxThreshold);
    }

    public JdkZlibExtendEncoder(boolean enableCompress, int compressionLevel, long minThreshold, long maxThreshold) {
        super(compressionLevel);
        this.enableCompress = enableCompress;
        thresholdCheck(minThreshold, maxThreshold);
    }

    public JdkZlibExtendEncoder(boolean enableCompress, int compressionLevel, byte[] dictionary, long minThreshold, long maxThreshold) {
        super(compressionLevel, dictionary);
        this.enableCompress = enableCompress;
        thresholdCheck(minThreshold, maxThreshold);
    }

    private void thresholdCheck(long minThreshold, long maxThreshold) {
        if (enableCompress && (minThreshold < NOT_LIMIT || maxThreshold < NOT_LIMIT)) {
            throw new IllegalArgumentException("compress minThreshold or maxThreshold is illegal, minThreshold:" +
                    minThreshold + ", maxThreshold:" + maxThreshold);
        }
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf uncompressed, ByteBuf out) throws Exception {

        if (!enableCompress) {
            noCompress(uncompressed, out);
            return;
        }
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