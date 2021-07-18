package com.hex.netty.invoke;

import com.hex.netty.connection.ServerManagerImpl;
import com.hex.netty.protocol.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
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
    private CountDownLatch latch;
    private int requestTimeout = 30;
    private InetSocketAddress address;

    public ResponseFuture(String requestSeq, int requestTimeout, InetSocketAddress address) {
        this.requestSeq = requestSeq;
        this.requestTimeout = requestTimeout;
        this.address = address;
    }

    public ResponseFuture(String requestSeq, RpcCallback rpcCallback) {
        this.requestSeq = requestSeq;
        this.rpcCallback = rpcCallback;
    }

    /**
     * 等待服务端响应结果并返回
     */
    public RpcResponse waitForResponse() {
        latch = new CountDownLatch(1);
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
            logger.error("Request timed out! seq:[{}], max wait time:[{}]s", requestSeq, requestTimeout);
            // 记录错误次数
            ServerManagerImpl.serverError(address);
            return RpcResponse.requestTimeout(requestSeq);
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
