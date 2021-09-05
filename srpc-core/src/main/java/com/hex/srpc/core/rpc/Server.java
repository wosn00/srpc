package com.hex.srpc.core.rpc;

import java.util.Set;

/**
 * @author hs
 */
public interface Server {

    /**
     * 手动添加扫描包路径[可选]
     */
    Server configScanPackages(Set<String> packages);

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
