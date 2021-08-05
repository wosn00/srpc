package com.hex.netty.connection;

import com.hex.netty.protocol.Command;

import java.net.SocketAddress;

/**
 * @author: hs
 */
public interface Connection {

    String getId();

    void close();

    boolean isAvailable();

    SocketAddress getRemoteAddress();

    void send(Command<?> command);

    long getLastSendTime();
}
