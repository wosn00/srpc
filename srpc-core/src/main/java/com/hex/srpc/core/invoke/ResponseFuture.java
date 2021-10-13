package com.hex.srpc.core.invoke;

import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.node.NodeManager;
import com.hex.srpc.core.protocol.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: hs
 */
public class ResponseFuture {
    private static final Logger logger = LoggerFactory.getLogger(ResponseFuture.class);

    private Long requestSeq;
    private RpcResponse rpcResponse;
    private RpcCallback rpcCallback;
    private CountDownLatch latch = new CountDownLatch(1);
    private int requestTimeout;
    private HostAndPort remoteAddress;
    private static ThreadPoolExecutor TASK_EXECUTOR;

    public ResponseFuture(Long requestSeq, int requestTimeout, HostAndPort remoteAddress, RpcCallback rpcCallback) {
        this.requestSeq = requestSeq;
        this.requestTimeout = requestTimeout;
        this.rpcCallback = rpcCallback;
        this.remoteAddress = remoteAddress;
    }

    /**
     * 等待服务端响应结果并返回
     */
    public RpcResponse waitForResponse() {
        boolean await;
        try {
            await = latch.await(requestTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("wait for response error", e);
            return RpcResponse.clientError(requestSeq);
        }
        if (await && rpcResponse != null) {
            return rpcResponse;
        } else {
            // 响应超时
            logger.error("Request timed out! seq: {}, max wait time: {}s", requestSeq, requestTimeout);
            // 记录错误次数
            NodeManager.serverError(remoteAddress);
            return RpcResponse.responseTimeout(requestSeq);
        }
    }

    /**
     * 客户端收到服务端响应后调用
     */
    public void receipt() {
        if (latch != null) {
            latch.countDown();
        }
        // 执行响应回调方法
        if (this.rpcCallback != null) {
            try {
                if (TASK_EXECUTOR != null) {
                    TASK_EXECUTOR.execute(() -> rpcCallback.callback(this.rpcResponse));
                } else {
                    rpcCallback.callback(this.rpcResponse);
                }
            } catch (Exception e) {
                logger.error("response callback processing failed!,requestSeq:{}", this.requestSeq, e);
            }
        }
    }

    public void setRpcResponse(RpcResponse rpcResponse) {
        this.rpcResponse = rpcResponse;
    }

    public Long getRequestSeq() {
        return requestSeq;
    }

    public void setRequestSeq(Long requestSeq) {
        this.requestSeq = requestSeq;
    }

    public static void setTaskExecutor(ThreadPoolExecutor taskExecutor) {
        TASK_EXECUTOR = taskExecutor;
    }
}
