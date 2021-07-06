package com.hex.netty.config;

import com.hex.netty.protocol.adpater.PbProtocolAdapter;
import com.hex.netty.protocol.adpater.ProtocolAdapter;

/**
 * @author: hs
 */
public class RpcClientConfig {

    private Integer eventLoopGroupSelector = 4;

    private Integer workerThreads = 10;

    private Integer connectionTimeout = 3000;

    private Integer sendBuf = 65535;

    private Integer receiveBuf = 65535;

    private Integer lowWaterLevel = 1024 * 1024;

    private Integer highWaterLevel = 10 * 1024 * 1024;

    private Integer maxIdleSecs = 180;

    private ProtocolAdapter protocolAdapter = new PbProtocolAdapter();

    public Integer getEventLoopGroupSelector() {
        return eventLoopGroupSelector;
    }

    public void setEventLoopGroupSelector(Integer eventLoopGroupSelector) {
        this.eventLoopGroupSelector = eventLoopGroupSelector;
    }

    public Integer getWorkerThreads() {
        return workerThreads;
    }

    public void setWorkerThreads(Integer workerThreads) {
        this.workerThreads = workerThreads;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    public Integer getSendBuf() {
        return sendBuf;
    }

    public void setSendBuf(Integer sendBuf) {
        this.sendBuf = sendBuf;
    }

    public Integer getReceiveBuf() {
        return receiveBuf;
    }

    public void setReceiveBuf(Integer receiveBuf) {
        this.receiveBuf = receiveBuf;
    }

    public Integer getLowWaterLevel() {
        return lowWaterLevel;
    }

    public void setLowWaterLevel(Integer lowWaterLevel) {
        this.lowWaterLevel = lowWaterLevel;
    }

    public Integer getHighWaterLevel() {
        return highWaterLevel;
    }

    public void setHighWaterLevel(Integer highWaterLevel) {
        this.highWaterLevel = highWaterLevel;
    }

    public Integer getMaxIdleSecs() {
        return maxIdleSecs;
    }

    public void setMaxIdleSecs(Integer maxIdleSecs) {
        this.maxIdleSecs = maxIdleSecs;
    }

    public ProtocolAdapter getProtocolAdapter() {
        return protocolAdapter;
    }

    public void setProtocolAdapter(ProtocolAdapter protocolAdapter) {
        this.protocolAdapter = protocolAdapter;
    }
}
