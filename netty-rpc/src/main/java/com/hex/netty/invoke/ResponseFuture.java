package com.hex.netty.invoke;

import com.hex.netty.node.HostAndPort;
import com.hex.netty.node.NodeManager;
import com.hex.netty.protocol.Command;
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

    private Long requestSeq;
    private Command<String> rpcResponse;
    private RpcCallback rpcCallback;
    private CountDownLatch latch;
    private int requestTimeout = 30;
    private HostAndPort remoteAddress;

    public ResponseFuture(Long requestSeq, int requestTimeout, HostAndPort remoteAddress) {
        this.requestSeq = requestSeq;
        this.requestTimeout = requestTimeout;
        this.remoteAddress = remoteAddress;
    }

    public ResponseFuture(Long requestSeq, int requestTimeout, HostAndPort remoteAddress, RpcCallback rpcCallback) {
        this.requestSeq = requestSeq;
        this.requestTimeout = requestTimeout;
        this.rpcCallback = rpcCallback;
        this.remoteAddress = remoteAddress;
    }

    /**
     * 等待服务端响应结果并返回
     */
    public Command<String> waitForResponse() {
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
            logger.error("Request timed out! seq:{}, max wait time:{}s", requestSeq, requestTimeout);
            // 记录错误次数
            NodeManager.serverError(remoteAddress);
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
                rpcCallback.callback((RpcResponse) this.rpcResponse);
            } catch (Exception e) {
                logger.error("response callback processing failed!,requestSeq:{}", this.requestSeq, e);
            }
        }
    }

    public void setRpcResponse(Command rpcResponse) {
        this.rpcResponse = rpcResponse;
    }

    public Long getRequestSeq() {
        return requestSeq;
    }

    public void setRequestSeq(Long requestSeq) {
        this.requestSeq = requestSeq;
    }
}
