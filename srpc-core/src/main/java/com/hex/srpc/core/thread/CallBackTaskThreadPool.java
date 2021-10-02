package com.hex.srpc.core.thread;

import com.hex.common.annotation.SPI;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author: hs
 */
@SPI
public interface CallBackTaskThreadPool {

    /**
     * 使用SPI获取自定义回调任务线程池
     *
     * @return 线程池
     */
    ThreadPoolExecutor getCallBackTaskThreadPool();
}
