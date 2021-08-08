package com.hex.netty.connection;

import com.hex.netty.node.HostAndPort;
import com.hex.netty.protocol.Command;
import com.hex.netty.protocol.adpater.PbProtocolAdapter;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: hs
 */
public class Connection implements IConnection {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final AttributeKey<IConnection> CONN = AttributeKey.valueOf("CONNECTION");

    private Long id;

    private Channel channel;

    private AtomicBoolean isClosed = new AtomicBoolean(false);

    private long lastSendTime = System.currentTimeMillis();

    public Connection(Long id) {
        this.id = id;
    }

    public Connection(Long id, Channel channel) {
        this.id = id;
        this.channel = channel;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void close() {
        if (isClosed.compareAndSet(false, true)) {
            logger.warn("connection close! id=[{}]", id);
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
    public HostAndPort getRemoteAddress() {
        return HostAndPort.from((InetSocketAddress) channel.remoteAddress());
    }

    @Override
    public void send(Command command) {
        if (this.channel.isWritable() && isAvailable()) {
            this.channel.writeAndFlush(PbProtocolAdapter.getAdapter().encode(command));
            this.lastSendTime = System.currentTimeMillis();
        } else {
            logger.warn("connection is unWritable now ,id=[{}], command=[{}]", id, command);
            close();
        }
    }

    @Override
    public long getLastSendTime() {
        return lastSendTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Connection that = (Connection) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
