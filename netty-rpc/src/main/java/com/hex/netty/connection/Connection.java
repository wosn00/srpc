package com.hex.netty.connection;

import com.hex.netty.protocol.Command;

/**
 * @author: hs
 */
public interface Connection {

    String getId();

    void close();

    boolean isAvailable();

    void send(Command command);
}
