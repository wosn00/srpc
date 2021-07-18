package com.hex.netty.chain;

import com.google.common.collect.Maps;
import com.hex.netty.connection.Connection;
import com.hex.netty.connection.ServerManager;
import com.hex.netty.protocol.Command;

import java.util.Map;

/**
 * @author: hs
 */
public class DealingContext {

    private Command<String> command;

    private DealingChain dealingChain;

    private ServerManager serverManager;

    private Connection connection;

    private long createTime = System.currentTimeMillis();

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

    public Command<String> getCommand() {
        return command;
    }

    public void setCommand(Command<String> command) {
        this.command = command;
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

    public ServerManager getServerManager() {
        return serverManager;
    }

    public void setServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
