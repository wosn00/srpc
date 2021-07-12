package com.hex.netty.rpc;

/**
 * @author hs
 */
public interface Server {

    /**
     * 启动服务端，使用配置里的端口
     */
    Server start();

    /**
     * 启动服务端，使用指定端口
     */
    Server startAtPort(int port);

    /**
     * 停止服务端
     */
    void stop();
}
