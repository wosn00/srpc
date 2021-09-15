package com.hex.srpc.core.config;


import com.hex.common.constant.LoadBalanceRule;
import com.hex.common.constant.RpcConstant;

/**
 * @author: hs
 */
public class SRpcClientConfig extends TLSConfig {

    private Integer selectorThreads = RpcConstant.DEFAULT_THREADS; //io线程数
    private Integer workerThreads = RpcConstant.DEFAULT_THREADS; //工作线程数

    private Integer connectionTimeout = 5; //连接超时时间(秒)
    private Integer requestTimeout = 10; //请求超时时间(秒)
    private Integer connectionSizePerNode = 5; //每个节点连接数
    private LoadBalanceRule loadBalanceRule = LoadBalanceRule.CONSISTENT_HASH; //多节点负载均衡策略
    private Integer connectionIdleTime = 180; //超过连接空闲时间(秒)未收发数据则关闭连接
    private Integer heartBeatTimeInterval = 30; //发送心跳包间隔时间(秒)
    private Integer serverHealthCheckTimeInterval = 30; //rpc服务健康检查周期(秒),恢复不可用的rpc服务

    private Integer sendBuf = 65535; //tcp发送缓冲区
    private Integer receiveBuf = 65535; //tcp接收缓冲区
    private Integer lowWaterLevel = 1024 * 1024; //低水位
    private Integer highWaterLevel = 10 * 1024 * 1024; //高水位

    private Boolean preventDuplicateEnable = true; //是否开启去重处理

    private Boolean trafficMonitorEnable = true; //是否开启流控
    private Long maxReadSpeed = 10 * 1000 * 1000L; //带宽限制，最大读取速度
    private Long maxWriteSpeed = 10 * 1000 * 1000L; //带宽限制，最大写出速度

    private Boolean compressEnable = true; //是否开启数据压缩（平均压缩率在60%以上，可节省大部分流量，性能损耗低）
    private Long minThreshold = -1L; //开启压缩包大小最低阈值(byte),超过则压缩,-1代表不限制
    private Long maxThreshold = -1L; //开启压缩包大小最高阈值(byte),低于则压缩,-1代表不限制

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


    public Boolean getCompressEnable() {
        return compressEnable;
    }

    public SRpcClientConfig setCompressEnable(Boolean compressEnable) {
        this.compressEnable = compressEnable;
        return this;
    }

    public Long getMinThreshold() {
        return minThreshold;
    }

    public SRpcClientConfig setMinThreshold(Long minThreshold) {
        this.minThreshold = minThreshold;
        return this;
    }

    public Long getMaxThreshold() {
        return maxThreshold;
    }

    public SRpcClientConfig setMaxThreshold(Long maxThreshold) {
        this.maxThreshold = maxThreshold;
        return this;
    }

    public Boolean getTrafficMonitorEnable() {
        return trafficMonitorEnable;
    }

    public SRpcClientConfig setTrafficMonitorEnable(Boolean trafficMonitorEnable) {
        this.trafficMonitorEnable = trafficMonitorEnable;
        return this;
    }

    public Long getMaxReadSpeed() {
        return maxReadSpeed;
    }

    public SRpcClientConfig setMaxReadSpeed(Long maxReadSpeed) {
        this.maxReadSpeed = maxReadSpeed;
        return this;
    }

    public Long getMaxWriteSpeed() {
        return maxWriteSpeed;
    }

    public SRpcClientConfig setMaxWriteSpeed(Long maxWriteSpeed) {
        this.maxWriteSpeed = maxWriteSpeed;
        return this;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public Boolean getPreventDuplicateEnable() {
        return preventDuplicateEnable;
    }

    public void setPreventDuplicateEnable(Boolean preventDuplicateEnable) {
        this.preventDuplicateEnable = preventDuplicateEnable;
    }

    public Integer getConnectionSizePerNode() {
        return connectionSizePerNode;
    }

    public void setConnectionSizePerNode(Integer connectionSizePerNode) {
        this.connectionSizePerNode = connectionSizePerNode;
    }

    public LoadBalanceRule getLoadBalanceRule() {
        return loadBalanceRule;
    }

    public void setLoadBalanceRule(LoadBalanceRule loadBalanceRule) {
        this.loadBalanceRule = loadBalanceRule;
    }

    public Integer getConnectionIdleTime() {
        return connectionIdleTime;
    }

    public SRpcClientConfig setConnectionIdleTime(Integer connectionIdleTime) {
        this.connectionIdleTime = connectionIdleTime;
        return this;
    }

    public Integer getHeartBeatTimeInterval() {
        return heartBeatTimeInterval;
    }

    public SRpcClientConfig setHeartBeatTimeInterval(Integer heartBeatTimeInterval) {
        this.heartBeatTimeInterval = heartBeatTimeInterval;
        return this;
    }

    public Integer getServerHealthCheckTimeInterval() {
        return serverHealthCheckTimeInterval;
    }

    public SRpcClientConfig setServerHealthCheckTimeInterval(Integer serverHealthCheckTimeInterval) {
        this.serverHealthCheckTimeInterval = serverHealthCheckTimeInterval;
        return this;
    }
}
