package com.hex.srpc.core.rpc.task;

import com.hex.srpc.core.node.HostAndPort;
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
            if (!nodeStatus.isAvailable()) {
                //发送心跳包探测
                boolean avaiable = nodeManager.getClient().sendHeartBeat(nodeStatus.getNode());
                if (Boolean.TRUE.equals(avaiable)) {
                    //心跳成功则置0
                    serverStatusMap.remove(nodeStatus.getNode());
                }
            }
        }


    }
}
