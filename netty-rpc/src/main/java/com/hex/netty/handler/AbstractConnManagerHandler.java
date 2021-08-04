package com.hex.netty.handler;

import com.hex.netty.connection.Connection;
import com.hex.netty.connection.ConnectionPool;
import com.hex.netty.connection.ServerManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

import static com.hex.netty.connection.NettyConnection.CONN;

/**
 * @author: hs
 */
abstract class AbstractConnManagerHandler extends ChannelDuplexHandler {

    ServerManager serverManager;

    void close(ChannelHandlerContext ctx) {
        //获取连接
        Connection connection = ctx.channel().attr(CONN).get();

        InetSocketAddress node = (InetSocketAddress) ctx.channel().remoteAddress();
        //获取对应节点的连接池
        ConnectionPool connectionPool = serverManager.getConnectionPool(node);
        //关闭连接
        connectionPool.releaseConnection(connection.getId());
    }
}
