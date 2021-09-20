package com.hex.srpc.core.rpc.task;

import com.hex.common.net.HostAndPort;
import com.hex.srpc.core.node.NodeStatus;
import com.hex.srpc.core.node.INodeManager;

import java.util.Map;

/**
 * @author: hs
 * <p>
 * 服务健康状态检查
 */
public class NodeHealthCheckTask implements Runnable {

    private INodeManager nodeManager;

    public NodeHealthCheckTask(INodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    @Override
    public void run() {
        Map<HostAndPort, NodeStatus> serverStatusMap = nodeManager.getNodeStatusMap();
        for (NodeStatus nodeStatus : serverStatusMap.values()) {
            if (!nodeStatus.isErrorOccurred()) {
                //发送心跳包探测
                boolean available = nodeManager.getClient().sendHeartBeat(nodeStatus.getNode());
                if (Boolean.TRUE.equals(available)) {
                    //心跳成功则重置错误次数
                    nodeStatus.resetErrorTimes();
                }
            }
        }
    }
}
