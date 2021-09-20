package com.hex.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: hs
 */
public class SRpcThreadFactory implements ThreadFactory {
    private AtomicInteger threadNumber = new AtomicInteger(1);
    private String prefix;
    private static volatile SRpcThreadFactory defaultFactory;

    public SRpcThreadFactory(String prefix) {
        this.prefix = prefix + "-" + "thread-";
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, this.prefix + threadNumber.getAndIncrement());
        thread.setDaemon(true);
        return thread;
    }

    public static SRpcThreadFactory getDefault() {
        if (defaultFactory == null) {
            synchronized (SRpcThreadFactory.class) {
                if (defaultFactory == null) {
                    defaultFactory = new SRpcThreadFactory("srpc");
                }
            }
        }
        return defaultFactory;
    }

}
