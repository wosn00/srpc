package com.hex.netty.handler;

import com.hex.netty.connection.IConnection;
import com.hex.netty.connection.IConnectionPool;
import com.hex.netty.node.HostAndPort;
import com.hex.netty.node.INodeManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

import static com.hex.netty.connection.Connection.CONN;

/**
 * @author: hs
 */
abstract class AbstractConnManagerHandler extends ChannelDuplexHandler {

    INodeManager nodeManager;

    void close(ChannelHandlerContext ctx) {
        //获取连接
        IConnection connection = ctx.channel().attr(CONN).get();

        HostAndPort node = HostAndPort.from((InetSocketAddress) ctx.channel().remoteAddress());
        //获取对应节点的连接池
        IConnectionPool connectionPool = nodeManager.getConnectionPool(node);
        //关闭连接
        connectionPool.releaseConnection(connection.getId());
    }
}
