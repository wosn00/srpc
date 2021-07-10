package com.hex.netty.config;


/**
 * @author: hs
 */
public class RpcClientConfig {

    private Integer selectorThreads = 8;
    private Integer workerThreads = 8;
    private Integer connectionTimeout = 5000;
    private Integer requestTimeout = 30;
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
     * 带宽限制，最大读取速度
     */
    private Long maxReadSpeed = 10 * 1000 * 1000L;

    /**
     * 带宽限制，最大写出速度
     */
    private Long maxWriteSpeed = 10 * 1000 * 1000L;

    /**
     * 是否开启数据压缩（经压测，平均压缩率在60%以上，可节省大部分流量，性能损耗可忽略）
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

    public Integer getSelectorThreads() {
        return selectorThreads;
    }

    public void setSelectorThreads(Integer selectorThreads) {
        this.selectorThreads = selectorThreads;
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

    public RpcClientConfig setCompressEnable(Boolean compressEnable) {
        this.compressEnable = compressEnable;
        return this;
    }

    public Integer getCompressionLevel() {
        return compressionLevel;
    }

    public RpcClientConfig setCompressionLevel(Integer compressionLevel) {
        this.compressionLevel = compressionLevel;
        return this;
    }

    public Long getMinThreshold() {
        return minThreshold;
    }

    public RpcClientConfig setMinThreshold(Long minThreshold) {
        this.minThreshold = minThreshold;
        return this;
    }

    public Long getMaxThreshold() {
        return maxThreshold;
    }

    public RpcClientConfig setMaxThreshold(Long maxThreshold) {
        this.maxThreshold = maxThreshold;
        return this;
    }

    public Boolean getTrafficMonitorEnable() {
        return trafficMonitorEnable;
    }

    public RpcClientConfig setTrafficMonitorEnable(Boolean trafficMonitorEnable) {
        this.trafficMonitorEnable = trafficMonitorEnable;
        return this;
    }

    public Long getMaxReadSpeed() {
        return maxReadSpeed;
    }

    public RpcClientConfig setMaxReadSpeed(Long maxReadSpeed) {
        this.maxReadSpeed = maxReadSpeed;
        return this;
    }

    public Long getMaxWriteSpeed() {
        return maxWriteSpeed;
    }

    public RpcClientConfig setMaxWriteSpeed(Long maxWriteSpeed) {
        this.maxWriteSpeed = maxWriteSpeed;
        return this;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }
}
