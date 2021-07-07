package com.hex.netty.connection;

import com.hex.netty.protocol.Command;
import com.hex.netty.protocol.adpater.PbProtocolAdapter;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: hs
 */
public class NettyConnection implements Connection {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final AttributeKey<Connection> CONN = AttributeKey.valueOf("CONNECTION");

    private String id;

    private Channel channel;

    private AtomicBoolean isClosed = new AtomicBoolean(false);

    public NettyConnection(String id) {
        this.id = id;
    }

    public NettyConnection(String id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            logger.warn("connection close! id=[{}]!", id);
            try {
                this.channel.close();
            } catch (Exception e) {
                logger.error("connection close failed!", e);
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return channel != null && !isClosed.get() && this.channel.isActive();
    }

    @Override
    public SocketAddress getRemoteAddress() {
        return channel.remoteAddress();
    }

    @Override
    public void send(Command command) {
        if (this.channel.isWritable() && isAvailable()) {
            this.channel.writeAndFlush(PbProtocolAdapter.getAdapter().encode(command));
        } else {
            logger.warn("connection is unWritable now!,id=[{}], command=[{}]", id, command);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NettyConnection that = (NettyConnection) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
