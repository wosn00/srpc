package com.hex.netty.connection;

import com.hex.netty.node.HostAndPort;
import com.hex.netty.protocol.Command;

/**
 * @author: hs
 * <p>
 * 连接
 */
public interface IConnection {

    String getId();

    void close();

    boolean isAvailable();

    HostAndPort getRemoteAddress();

    void send(Command<?> command);

    long getLastSendTime();
}
