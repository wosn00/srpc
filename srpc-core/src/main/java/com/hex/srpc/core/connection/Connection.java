package com.hex.srpc.core.connection;

import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.protocol.Command;
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
    private static final Logger logger = LoggerFactory.getLogger(Connection.class);

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
            logger.warn("connection close! id={}", id);
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
            this.channel.writeAndFlush(command);
            this.lastSendTime = System.currentTimeMillis();
        } else {
            logger.warn("connection is unWritable now ,id={}, command={}", id, command);
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
