package com.hex.netty.config;

/**
 * @author: hs
 */
public class RpcServerConfig {

    private Integer port = 8008;
    private Integer eventLoopGroupSelector = 4;
    private Integer workerThreads = 10;
    private Integer connectionTimeout = 3000;
    private Integer sendBuf = 65535;
    private Integer receiveBuf = 65535;
    private Integer lowWaterLevel = 1024 * 1024;
    private Integer highWaterLevel = 10 * 1024 * 1024;
    private Integer maxIdleSecs = 180;

    /**
     * 是否开启流量监控
     */
    private Boolean trafficMonitorEnable = true;

    /**
     * 带宽限制，最大读取速度 bytes/s
     */
    private Long maxReadSpeed = 10 * 1000 * 1000L;

    /**
     * 带宽限制，最大写出速度 bytes/s
     */
    private Long maxWriteSpeed = 10 * 1000 * 1000L;

    /**
     * 是否开启压缩
     */
    private Boolean compressEnable = true;

    /**
     * 压缩级别(0-9)
     */
    private Integer compressionLevel = 6;

    /**
     * 开启压缩最低阈值(byte)
     */
    private Long minThreshold = -1L;

    /**
     * 开启压缩最高阈值(byte)
     */
    private Long maxThreshold = -1L;


    public Integer getEventLoopGroupSelector() {
        return eventLoopGroupSelector;
    }

    public void setEventLoopGroupSelector(Integer eventLoopGroupSelector) {
        this.eventLoopGroupSelector = eventLoopGroupSelector;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
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

    public Boolean getCompressEnable() {
        return compressEnable;
    }

    public RpcServerConfig setCompressEnable(Boolean compressEnable) {
        this.compressEnable = compressEnable;
        return this;
    }

    public Integer getCompressionLevel() {
        return compressionLevel;
    }

    public RpcServerConfig setCompressionLevel(Integer compressionLevel) {
        this.compressionLevel = compressionLevel;
        return this;
    }

    public Long getMinThreshold() {
        return minThreshold;
    }

    public RpcServerConfig setMinThreshold(Long minThreshold) {
        this.minThreshold = minThreshold;
        return this;
    }

    public Long getMaxThreshold() {
        return maxThreshold;
    }

    public RpcServerConfig setMaxThreshold(Long maxThreshold) {
        this.maxThreshold = maxThreshold;
        return this;
    }

    public Boolean getTrafficMonitorEnable() {
        return trafficMonitorEnable;
    }

    public RpcServerConfig setTrafficMonitorEnable(Boolean trafficMonitorEnable) {
        this.trafficMonitorEnable = trafficMonitorEnable;
        return this;
    }

    public Long getMaxReadSpeed() {
        return maxReadSpeed;
    }

    public RpcServerConfig setMaxReadSpeed(Long maxReadSpeed) {
        this.maxReadSpeed = maxReadSpeed;
        return this;
    }

    public Long getMaxWriteSpeed() {
        return maxWriteSpeed;
    }

    public RpcServerConfig setMaxWriteSpeed(Long maxWriteSpeed) {
        this.maxWriteSpeed = maxWriteSpeed;
        return this;
    }
}
