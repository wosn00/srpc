package com.hex.srpc.core.rpc.task;

import com.hex.srpc.core.node.INodeManager;
import com.hex.common.utils.SerializerUtil;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author: hs
 */
public class ConnectionNumCountTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ConnectionNumCountTask.class);

    private INodeManager nodeManager;

    public ConnectionNumCountTask(INodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    public void run() {
        Map<String, AtomicInteger> nodeConnectionSizeMap = nodeManager.getConnectionSize();
        if (MapUtils.isEmpty(nodeConnectionSizeMap)) {
            logger.info("服务端当前总连接数: 0");
            return;
        }
        //排除掉连接数为0的节点
        for (Map.Entry<String, AtomicInteger> entry : nodeConnectionSizeMap.entrySet()) {
            if (entry.getValue().get() == 0) {
                nodeConnectionSizeMap.remove(entry.getKey());
            }
        }
        int sum = nodeConnectionSizeMap.values().stream().mapToInt(AtomicInteger::get).sum();

        if (logger.isInfoEnabled()) {
            logger.info("服务端当前总连接数量: {}, 客户端地址: {}", sum, SerializerUtil.serializePretty(nodeConnectionSizeMap));
        }
    }
}
