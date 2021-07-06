package com.hex.netty.handler;

import com.hex.netty.connection.Connection;
import com.hex.netty.connection.DefaultConnectionManager;
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

    DefaultConnectionManager defaultConnectionManager;

    void close(ChannelHandlerContext ctx) {
        logger.info("trigger channel close!");
        Connection connection = ctx.channel().attr(CONN).get();
        connection.close();
        defaultConnectionManager.removeConn(connection.getId());
    }
}
