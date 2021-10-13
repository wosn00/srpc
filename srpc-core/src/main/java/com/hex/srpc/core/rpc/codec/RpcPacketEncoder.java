package com.hex.srpc.core.rpc.codec;

import com.hex.common.constant.CompressType;
import com.hex.common.constant.RpcConstant;
import com.hex.common.constant.SerializeType;
import com.hex.common.exception.EncoderException;
import com.hex.common.spi.ExtensionLoader;
import com.hex.srpc.core.protocol.Command;
import com.hex.srpc.core.rpc.compress.Compress;
import com.hex.srpc.core.rpc.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hs
 */
public class RpcPacketEncoder extends MessageToByteEncoder<Command> {
    private static final Logger logger = LoggerFactory.getLogger(RpcPacketEncoder.class);

    private CompressType compressType;
    private Compress compress;
    private SerializeType serializerType;
    private Serializer serializer;

    public RpcPacketEncoder(CompressType compressType, SerializeType serializerType) {
        this.compressType = compressType;
        this.serializerType = serializerType;
        setCompress();
        setSerializer();
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Command command, ByteBuf out) throws Exception {
        try {
            out.writeShort(RpcConstant.MAGIC_NUMBER);
            out.writeByte(RpcConstant.VERSION);

            out.writeByte((byte) (serializerType.getCode() << 4 | compressType.getCode() & 0xff));
            out.writeBoolean(command.isRequest());

            byte[] serializeBytes = serializer.serialize(command);
            byte[] compressBytes = this.compress.compress(serializeBytes);
            out.writeInt(compressBytes.length);

            out.writeBytes(compressBytes);
        } catch (Exception e) {
            logger.error("frame encode failed", e);
            throw new EncoderException();
        }
    }

    private void setCompress() {
        ExtensionLoader<Compress> compressLoader = ExtensionLoader.getExtensionLoader(Compress.class);
        this.compress = compressLoader.getExtension(compressType.getName());
    }

    private void setSerializer() {
        ExtensionLoader<Serializer> serializerLoader = ExtensionLoader.getExtensionLoader(Serializer.class);
        this.serializer = serializerLoader.getExtension(serializerType.getName());
    }
}
