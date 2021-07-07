package com.hex.netty.rpc;

/**
 * @author hs
 */
public interface Server {

    /**
     * 启动服务端
     */
    void start();

    /**
     * 停止服务端
     */
    void stop();
}
