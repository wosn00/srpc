package com.hex.netty.handler;

import com.hex.netty.connection.Connection;
import com.hex.netty.connection.ConnectionManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.hex.netty.connection.NettyConnection.CONN;

/**
 * @author: hs
 */
abstract class AbstractConnManagerHandler extends ChannelDuplexHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    ConnectionManager connectionManager;

    void close(ChannelHandlerContext ctx) {
        Connection connection = ctx.channel().attr(CONN).get();
        connection.close();
        connectionManager.removeConn(connection.getId());
    }
}
