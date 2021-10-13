package com.hex.srpc.core.chain;

import com.google.common.collect.Maps;
import com.hex.srpc.core.connection.IConnection;
import com.hex.srpc.core.node.INodeManager;
import com.hex.srpc.core.protocol.Command;

import java.util.Map;

/**
 * @author: hs
 */
public class DealingContext {

    private boolean isClient;
    private Command command;
    private DealingChain dealingChain;
    private INodeManager nodeManager;
    private IConnection connection;
    private long createTime = System.currentTimeMillis();
    private boolean isPrintHeartbeatInfo;
    /**
     * 用于各个dealing自定义存放内容
     */
    private Map<String, Object> content = Maps.newHashMap();

    /**
     * 执行下一个处理器
     */
    public void nextDealing() {
        dealingChain.deal(this);
    }

    public boolean isClient() {
        return isClient;
    }

    public DealingContext setClient(boolean client) {
        isClient = client;
        return this;
    }

    public Command getCommand() {
        return command;
    }

    public DealingContext setCommand(Command command) {
        this.command = command;
        return this;
    }

    public Map<String, Object> getContent() {
        return content;
    }

    public DealingContext setContent(Map<String, Object> content) {
        this.content = content;
        return this;
    }

    public Object attr(String key) {
        return content.get(key);
    }

    public void attr(String key, Object value) {
        content.put(key, value);
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public DealingChain getDealingChain() {
        return dealingChain;
    }

    public void setDealingChain(DealingChain dealingChain) {
        this.dealingChain = dealingChain;
    }

    public INodeManager getNodeManager() {
        return nodeManager;
    }

    public void setNodeManager(INodeManager nodeManager) {
        this.nodeManager = nodeManager;
    }

    public IConnection getConnection() {
        return connection;
    }

    public void setConnection(IConnection connection) {
        this.connection = connection;
    }

    public boolean isPrintHeartbeatInfo() {
        return isPrintHeartbeatInfo;
    }

    public DealingContext setPrintHeartbeatInfo(boolean printHeartbeatInfo) {
        isPrintHeartbeatInfo = printHeartbeatInfo;
        return this;
    }
}
