package com.hex.srpc.core.thread;

import com.hex.common.annotation.SPI;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: hs
 */
@SPI
public interface BusinessThreadPool {

    /**
     * 使用SPI获取自定义业务线程池
     *
     * @return 线程池
     */
    ThreadPoolExecutor getBusinessThreadPool();
}
