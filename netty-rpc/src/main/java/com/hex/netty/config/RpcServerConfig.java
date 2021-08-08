package com.hex.netty.config;


import com.hex.netty.constant.RpcConstant;

/**
 * @author: hs
 */
public class RpcServerConfig {

    private Integer port = 8008; //绑定端口
    private Integer selectorThreads = RpcConstant.DEFAULT_THREADS; //io线程数
    private Integer workerThreads = RpcConstant.DEFAULT_THREADS; //工作线程数

    private Integer connectionIdleTime = 180;//超过连接空闲时间(秒)未收发数据则关闭连接
    private Integer printConnectionNumInterval = 30; //打印服务端当前连接数时间间隔(秒), 0为不打印

    private Integer sendBuf = 65535; //tcp发送缓冲区
    private Integer receiveBuf = 65535; //tcp接收缓冲区
    private Integer lowWaterLevel = 1024 * 1024; //低水位
    private Integer highWaterLevel = 10 * 1024 * 1024; //高水位

    private Boolean preventDuplicateEnable = true; //是否开启去重处理

    private Boolean trafficMonitorEnable = true; //是否开启流控
    private Long maxReadSpeed = 10 * 1000 * 1000L; //带宽限制，最大读取速度
    private Long maxWriteSpeed = 10 * 1000 * 1000L; //带宽限制，最大写出速度

    private Boolean compressEnable = true; //是否开启数据压缩(平均压缩率在60%以上，可节省大部分流量，性能损耗低)
    private Long minThreshold = -1L; //开启压缩最低阈值(byte)
    private Long maxThreshold = -1L; //开启压缩最高阈值(byte)

    public RpcServerConfig() {
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

    public Integer getConnectionIdleTime() {
        return connectionIdleTime;
    }

    public RpcServerConfig setConnectionIdleTime(Integer connectionIdleTime) {
        this.connectionIdleTime = connectionIdleTime;
        return this;
    }

    public Integer getPrintConnectionNumInterval() {
        return printConnectionNumInterval;
    }

    public RpcServerConfig setPrintConnectionNumInterval(Integer printConnectionNumInterval) {
        this.printConnectionNumInterval = printConnectionNumInterval;
        return this;
    }
}
