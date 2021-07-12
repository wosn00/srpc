package com.hex.netty.config;

import com.hex.netty.reflection.RouteScanner;

/**
 * @author: hs
 */
public class RpcServerConfig {

    private Integer port = 8008;
    private Integer selectorThreads = 8;
    private Integer workerThreads = 8;
    private Integer connectionTimeout = 5000;
    private Integer sendBuf = 65535;
    private Integer receiveBuf = 65535;
    private Integer lowWaterLevel = 1024 * 1024;
    private Integer highWaterLevel = 10 * 1024 * 1024;
    private Boolean preventDuplicateEnable = true; // 是否开启去重处理

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

    public RpcServerConfig(Class<?> primarySources) {
        new RouteScanner(primarySources).san();
    }

    public Integer getSelectorThreads() {
        return selectorThreads;
    }

    public void setSelectorThreads(Integer selectorThreads) {
        this.selectorThreads = selectorThreads;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("port should be lesser 65535 and greater 0");
        }
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

    public Boolean getPreventDuplicateEnable() {
        return preventDuplicateEnable;
    }

    public void setPreventDuplicateEnable(Boolean preventDuplicateEnable) {
        this.preventDuplicateEnable = preventDuplicateEnable;
    }
}
