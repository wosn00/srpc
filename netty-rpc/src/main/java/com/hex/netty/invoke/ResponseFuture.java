package com.hex.netty.invoke;

import com.hex.netty.protocol.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author: hs
 */
public class ResponseFuture {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String requestSeq;

    private RpcResponse rpcResponse;

    private RpcCallback rpcCallback;

    private CountDownLatch latch = new CountDownLatch(1);

    private static final int MAX_WAIT_TIME = 20;

    public ResponseFuture(String requestSeq) {
        this.requestSeq = requestSeq;
    }

    public ResponseFuture(String requestSeq, RpcCallback rpcCallback) {
        this.requestSeq = requestSeq;
        this.rpcCallback = rpcCallback;
    }

    /**
     * 等待服务端响应结果并返回
     */
    public RpcResponse waitForResponse() {
        boolean await;
        try {
            await = latch.await(MAX_WAIT_TIME, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("wait for response error", e);
            return RpcResponse.clientError(requestSeq);
        }
        if (await && rpcResponse != null) {
            return rpcResponse;
        } else {
            // 响应超时
            logger.error("Request timed out! seq:[{}], max wait time:[{}]s", requestSeq, MAX_WAIT_TIME);
            return RpcResponse.requestTimeout(requestSeq);
        }
    }

    /**
     * 客户端收到服务端响应后调用
     */
    public void receipt() {
        latch.countDown();
        // 执行响应回调方法
        if (this.rpcCallback != null) {
            try {
                rpcCallback.callback(this.rpcResponse);
            } catch (Exception e) {
                logger.error("response callback processing failed!,requestSeq:[{}]", this.requestSeq, e);
            }
        }
    }

    public RpcResponse getRpcResponse() {
        return rpcResponse;
    }

    public void setRpcResponse(RpcResponse rpcResponse) {
        this.rpcResponse = rpcResponse;
    }

    public String getRequestSeq() {
        return requestSeq;
    }

    public void setRequestSeq(String requestSeq) {
        this.requestSeq = requestSeq;
    }
}
