package com.hex.srpc.core.config;


import com.hex.common.constant.LoadBalanceRule;
import com.hex.common.constant.RpcConstant;

/**
 * @author: hs
 */
public class SRpcClientConfig extends TLSConfig {

    private Integer channelWorkerThreads = RpcConstant.DEFAULT_THREADS; //channel处理工作线程数，连接数量多时可调大

    private Integer callBackTaskThreads = 200; //回调任务处理线程池，0为不设置
    private Integer callBackTaskQueueSize = 500; //回调任务线程池队列大小

    private Integer connectionTimeout = 5; //连接超时时间(秒)
    private Integer requestTimeout = 10; //请求超时时间(秒)
    private Integer connectionSizePerNode = 3; //每个节点连接数
    private Integer connectionIdleTime = 180; //超过连接空闲时间(秒)未收发数据则关闭连接
    private Integer heartBeatTimeInterval = 30; //发送心跳包间隔时间(秒)

    private LoadBalanceRule loadBalanceRule = LoadBalanceRule.RANDOM; //集群负载均衡策略
    private boolean excludeUnAvailableNodesEnable = true; //集群模式下是否排除不可用的节点
    private Integer nodeErrorTimes = 3; //节点连接或请求超时/异常超过设置次数则置为节点不可用
    private Integer nodeHealthCheckTimeInterval = 10; //节点健康检查周期(秒),心跳包响应成功则恢复不可用的节点

    private Integer sendBuf = 65535; //tcp发送缓冲区
    private Integer receiveBuf = 65535; //tcp接收缓冲区
    private Integer lowWaterLevel = 1024 * 1024; //netty低水位
    private Integer highWaterLevel = 10 * 1024 * 1024; //netty高水位

    private boolean trafficMonitorEnable = false; //是否开启流量控制
    private Long maxReadSpeed = 10 * 1000 * 1000L; //带宽限制，最大读取速度
    private Long maxWriteSpeed = 10 * 1000 * 1000L; //带宽限制，最大写出速度

    private boolean compressEnable = true; //是否开启数据压缩（平均压缩率在60%以上，可节省大部分流量，性能损耗低）
    private Long minThreshold = -1L; //开启压缩包大小最低阈值(byte),超过则压缩,-1代表不限制
    private Long maxThreshold = -1L; //开启压缩包大小最高阈值(byte),低于则压缩,-1代表不限制

    public Integer getChannelWorkerThreads() {
        return channelWorkerThreads;
    }

    public SRpcClientConfig setChannelWorkerThreads(Integer channelWorkerThreads) {
        this.channelWorkerThreads = channelWorkerThreads;
        return this;
    }

    public Integer getCallBackTaskThreads() {
        return callBackTaskThreads;
    }

    public SRpcClientConfig setCallBackTaskThreads(Integer callBackTaskThreads) {
        this.callBackTaskThreads = callBackTaskThreads;
        return this;
    }

    public Integer getCallBackTaskQueueSize() {
        return callBackTaskQueueSize;
    }

    public SRpcClientConfig setCallBackTaskQueueSize(Integer callBackTaskQueueSize) {
        this.callBackTaskQueueSize = callBackTaskQueueSize;
        return this;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public SRpcClientConfig setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public SRpcClientConfig setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }

    public Integer getConnectionSizePerNode() {
        return connectionSizePerNode;
    }

    public SRpcClientConfig setConnectionSizePerNode(Integer connectionSizePerNode) {
        this.connectionSizePerNode = connectionSizePerNode;
        return this;
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

    public LoadBalanceRule getLoadBalanceRule() {
        return loadBalanceRule;
    }

    public SRpcClientConfig setLoadBalanceRule(LoadBalanceRule loadBalanceRule) {
        this.loadBalanceRule = loadBalanceRule;
        return this;
    }

    public boolean isExcludeUnAvailableNodesEnable() {
        return excludeUnAvailableNodesEnable;
    }

    public SRpcClientConfig setExcludeUnAvailableNodesEnable(boolean excludeUnAvailableNodesEnable) {
        this.excludeUnAvailableNodesEnable = excludeUnAvailableNodesEnable;
        return this;
    }

    public Integer getNodeErrorTimes() {
        return nodeErrorTimes;
    }

    public SRpcClientConfig setNodeErrorTimes(Integer nodeErrorTimes) {
        this.nodeErrorTimes = nodeErrorTimes;
        return this;
    }

    public Integer getNodeHealthCheckTimeInterval() {
        return nodeHealthCheckTimeInterval;
    }

    public SRpcClientConfig setNodeHealthCheckTimeInterval(Integer nodeHealthCheckTimeInterval) {
        this.nodeHealthCheckTimeInterval = nodeHealthCheckTimeInterval;
        return this;
    }

    public Integer getSendBuf() {
        return sendBuf;
    }

    public SRpcClientConfig setSendBuf(Integer sendBuf) {
        this.sendBuf = sendBuf;
        return this;
    }

    public Integer getReceiveBuf() {
        return receiveBuf;
    }

    public SRpcClientConfig setReceiveBuf(Integer receiveBuf) {
        this.receiveBuf = receiveBuf;
        return this;
    }

    public Integer getLowWaterLevel() {
        return lowWaterLevel;
    }

    public SRpcClientConfig setLowWaterLevel(Integer lowWaterLevel) {
        this.lowWaterLevel = lowWaterLevel;
        return this;
    }

    public Integer getHighWaterLevel() {
        return highWaterLevel;
    }

    public SRpcClientConfig setHighWaterLevel(Integer highWaterLevel) {
        this.highWaterLevel = highWaterLevel;
        return this;
    }

    public boolean isTrafficMonitorEnable() {
        return trafficMonitorEnable;
    }

    public SRpcClientConfig setTrafficMonitorEnable(boolean trafficMonitorEnable) {
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

    public boolean isCompressEnable() {
        return compressEnable;
    }

    public SRpcClientConfig setCompressEnable(boolean compressEnable) {
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
}
