package com.hex.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: hs
 */
public class SrpcThreadFactory implements ThreadFactory {
    private AtomicInteger threadNumber = new AtomicInteger(1);
    private String prefix;
    private static volatile SrpcThreadFactory defaultFactory;

    public SrpcThreadFactory(String prefix) {
        this.prefix = prefix + "-" + "-thread-";
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, this.prefix + threadNumber.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    }

    public static SrpcThreadFactory getDefault() {
        if (defaultFactory == null) {
            synchronized (SrpcThreadFactory.class) {
                if (defaultFactory == null) {
                    defaultFactory = new SrpcThreadFactory("rpc");
                }
            }
        }
        return defaultFactory;
    }

}
