package com.hex.common.utils;

import com.hex.common.thread.SRpcThreadFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: hs
 * <p>
 * 线程池工具类
 */
public class ThreadUtil {

    public static boolean isTerminated(Executor executor) {
        if (executor instanceof ExecutorService) {
            return ((ExecutorService) executor).isTerminated();
        }
        return false;
    }

    public static void gracefulShutdown(Executor executor, int timeout) {
        if (!(executor instanceof ExecutorService) || isTerminated(executor)) {
            return;
        }
        final ExecutorService es = (ExecutorService) executor;
        try {
            // shutdown后不允许再提交新的任务
            es.shutdown();
        } catch (SecurityException | NullPointerException ex2) {
            return;
        }
        try {
            // 指定时间内等待线程池处理完剩下的任务, 未执行完也立即关闭
            if (!es.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                es.shutdownNow();
            }
        } catch (InterruptedException ex) {
            es.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static ThreadPoolExecutor getFixThreadPoolExecutor(int coreThreads, int queueSize,
                                                              RejectedExecutionHandler rejectedExecutionHandler,
                                                              String threadFactoryName) {
        return new ThreadPoolExecutor(coreThreads, coreThreads, 0, TimeUnit.MILLISECONDS,
                queueSize == 0 ? new SynchronousQueue<>() :
                        (queueSize < 0 ? new LinkedBlockingQueue<>()
                                : new LinkedBlockingQueue<>(queueSize)),
                new SRpcThreadFactory(threadFactoryName), rejectedExecutionHandler);
    }
}
