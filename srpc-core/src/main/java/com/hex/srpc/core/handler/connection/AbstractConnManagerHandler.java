package com.hex.srpc.core.handler.connection;

import com.hex.srpc.core.connection.IConnection;
import com.hex.srpc.core.connection.IConnectionPool;
import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.node.INodeManager;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

import java.net.InetSocketAddress;

import static com.hex.srpc.core.connection.Connection.CONN;

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
