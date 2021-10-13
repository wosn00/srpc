package com.hex.srpc.core.connection;

import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.protocol.Command;

/**
 * @author: hs
 * <p>
 * 连接
 */
public interface IConnection {

    /**
     * 获取连接id
     *
     * @return 连接id
     */
    Long getId();

    /**
     * 关闭连接
     */
    void close();

    /**
     * 连接是否可用
     *
     * @return
     */
    boolean isAvailable();

    /**
     * 连接对端地址
     *
     * @return
     */
    HostAndPort getRemoteAddress();

    /**
     * 发送指令
     *
     * @param command
     */
    void send(Command command);

    /**
     * 获取最后一次发送时间
     *
     * @return
     */
    long getLastSendTime();
}
