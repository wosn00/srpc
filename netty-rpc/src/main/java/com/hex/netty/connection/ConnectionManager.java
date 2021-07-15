package com.hex.netty.connection;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author: hs
 */
public interface ConnectionManager {

    int size();

    Connection getConnection(List<InetSocketAddress> addresses);

    Connection getConnection();

}
