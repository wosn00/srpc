package com.hex.rpc.spring.starter.properties;

import com.hex.common.constant.CompressType;
import com.hex.common.constant.LoadBalanceRule;
import com.hex.common.constant.RpcConstant;
import com.hex.common.constant.SerializeType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author: hs
 */
@ConfigurationProperties(prefix = "srpc.client")
public class RpcClientProperties {
    private Integer channelWorkerThreads = RpcConstant.DEFAULT_THREADS; //channel处理工作线程数，连接数量多时可调大

    private Integer callBackTaskThreads = 200; //回调任务处理线程池，0为不设置
    private Integer callBackTaskQueueSize = 500; //回调任务线程池队列大小

    private Integer connectionTimeout = 5; //连接超时时间(秒)
    private Integer requestTimeout = 10; //请求超时时间(秒)
    private Integer connectionSizePerNode = 3; //每个节点连接数
    private Integer connectionIdleTime = 180; //超过连接空闲时间(秒)未收发数据则关闭连接
    private Integer heartBeatTimeInterval = 30; //发送心跳包间隔时间(秒)

    private CompressType compressType = CompressType.SNAPPY; //压缩算法类型，无需压缩为NONE
    private SerializeType serializeType = SerializeType.PROTOSTUFF; //序列化类型，默认protostuff

    private LoadBalanceRule loadBalanceRule = LoadBalanceRule.RANDOM; //集群负载均衡策略
    private boolean excludeUnAvailableNodesEnable = true; //集群模式下是否排除不可用的节点
    private Integer nodeErrorTimes = 3; //节点连接或请求超时/异常超过设置次数则置为节点不可用
    private Integer nodeHealthCheckTimeInterval = 10; //节点健康检查周期(秒),心跳包响应成功则恢复不可用的节点

    private Integer sendBuf = 65535; //tcp发送缓冲区
    private Integer receiveBuf = 65535; //tcp接收缓冲区
    private Integer lowWaterLevel = 1024 * 1024; //低水位
    private Integer highWaterLevel = 10 * 1024 * 1024; //高水位

    private Boolean trafficMonitorEnable = false; //是否开启流量控制
    private Long maxReadSpeed = 10 * 1000 * 1000L; //带宽限制，最大读取速度
    private Long maxWriteSpeed = 10 * 1000 * 1000L; //带宽限制，最大写出速度

    // TLS加密部分配置
    private Boolean useTLS = false; //是否开启TLS加密
    private String keyPath; //私钥文件路径
    private String keyPwd; //密码
    private String certPath; //证书文件路径
    private String trustCertPath; //受信任ca证书路径
    private String clientAuth; //是否要求客户端认证

    // 注册中心配置部分
    private Boolean enableRegistry = false; //是否使用注册中心
    private String registrySchema; //注册中心模式名称, 缺省为zookeeper
    private List<String> registryAddress; //注册中心地址

    public Integer getChannelWorkerThreads() {
        return channelWorkerThreads;
    }

    public RpcClientProperties setChannelWorkerThreads(Integer channelWorkerThreads) {
        this.channelWorkerThreads = channelWorkerThreads;
        return this;
    }

    public Integer getCallBackTaskThreads() {
        return callBackTaskThreads;
    }

    public RpcClientProperties setCallBackTaskThreads(Integer callBackTaskThreads) {
        this.callBackTaskThreads = callBackTaskThreads;
        return this;
    }

    public Integer getCallBackTaskQueueSize() {
        return callBackTaskQueueSize;
    }

    public RpcClientProperties setCallBackTaskQueueSize(Integer callBackTaskQueueSize) {
        this.callBackTaskQueueSize = callBackTaskQueueSize;
        return this;
    }

    public Integer getConnectionTimeout() {
        return connectionTimeout;
    }

    public RpcClientProperties setConnectionTimeout(Integer connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public Integer getRequestTimeout() {
        return requestTimeout;
    }

    public RpcClientProperties setRequestTimeout(Integer requestTimeout) {
        this.requestTimeout = requestTimeout;
        return this;
    }

    public Integer getConnectionSizePerNode() {
        return connectionSizePerNode;
    }

    public RpcClientProperties setConnectionSizePerNode(Integer connectionSizePerNode) {
        this.connectionSizePerNode = connectionSizePerNode;
        return this;
    }

    public Integer getConnectionIdleTime() {
        return connectionIdleTime;
    }

    public RpcClientProperties setConnectionIdleTime(Integer connectionIdleTime) {
        this.connectionIdleTime = connectionIdleTime;
        return this;
    }

    public Integer getHeartBeatTimeInterval() {
        return heartBeatTimeInterval;
    }

    public RpcClientProperties setHeartBeatTimeInterval(Integer heartBeatTimeInterval) {
        this.heartBeatTimeInterval = heartBeatTimeInterval;
        return this;
    }

    public LoadBalanceRule getLoadBalanceRule() {
        return loadBalanceRule;
    }

    public RpcClientProperties setLoadBalanceRule(LoadBalanceRule loadBalanceRule) {
        this.loadBalanceRule = loadBalanceRule;
        return this;
    }

    public boolean isExcludeUnAvailableNodesEnable() {
        return excludeUnAvailableNodesEnable;
    }

    public RpcClientProperties setExcludeUnAvailableNodesEnable(boolean excludeUnAvailableNodesEnable) {
        this.excludeUnAvailableNodesEnable = excludeUnAvailableNodesEnable;
        return this;
    }

    public Integer getNodeErrorTimes() {
        return nodeErrorTimes;
    }

    public RpcClientProperties setNodeErrorTimes(Integer nodeErrorTimes) {
        this.nodeErrorTimes = nodeErrorTimes;
        return this;
    }

    public Integer getNodeHealthCheckTimeInterval() {
        return nodeHealthCheckTimeInterval;
    }

    public RpcClientProperties setNodeHealthCheckTimeInterval(Integer nodeHealthCheckTimeInterval) {
        this.nodeHealthCheckTimeInterval = nodeHealthCheckTimeInterval;
        return this;
    }

    public Integer getSendBuf() {
        return sendBuf;
    }

    public RpcClientProperties setSendBuf(Integer sendBuf) {
        this.sendBuf = sendBuf;
        return this;
    }

    public Integer getReceiveBuf() {
        return receiveBuf;
    }

    public RpcClientProperties setReceiveBuf(Integer receiveBuf) {
        this.receiveBuf = receiveBuf;
        return this;
    }

    public Integer getLowWaterLevel() {
        return lowWaterLevel;
    }

    public RpcClientProperties setLowWaterLevel(Integer lowWaterLevel) {
        this.lowWaterLevel = lowWaterLevel;
        return this;
    }

    public Integer getHighWaterLevel() {
        return highWaterLevel;
    }

    public RpcClientProperties setHighWaterLevel(Integer highWaterLevel) {
        this.highWaterLevel = highWaterLevel;
        return this;
    }

    public Boolean getTrafficMonitorEnable() {
        return trafficMonitorEnable;
    }

    public RpcClientProperties setTrafficMonitorEnable(Boolean trafficMonitorEnable) {
        this.trafficMonitorEnable = trafficMonitorEnable;
        return this;
    }

    public Long getMaxReadSpeed() {
        return maxReadSpeed;
    }

    public RpcClientProperties setMaxReadSpeed(Long maxReadSpeed) {
        this.maxReadSpeed = maxReadSpeed;
        return this;
    }

    public Long getMaxWriteSpeed() {
        return maxWriteSpeed;
    }

    public RpcClientProperties setMaxWriteSpeed(Long maxWriteSpeed) {
        this.maxWriteSpeed = maxWriteSpeed;
        return this;
    }

    public Boolean getUseTLS() {
        return useTLS;
    }

    public RpcClientProperties setUseTLS(Boolean useTLS) {
        this.useTLS = useTLS;
        return this;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public RpcClientProperties setKeyPath(String keyPath) {
        this.keyPath = keyPath;
        return this;
    }

    public String getKeyPwd() {
        return keyPwd;
    }

    public RpcClientProperties setKeyPwd(String keyPwd) {
        this.keyPwd = keyPwd;
        return this;
    }

    public String getCertPath() {
        return certPath;
    }

    public RpcClientProperties setCertPath(String certPath) {
        this.certPath = certPath;
        return this;
    }

    public String getTrustCertPath() {
        return trustCertPath;
    }

    public RpcClientProperties setTrustCertPath(String trustCertPath) {
        this.trustCertPath = trustCertPath;
        return this;
    }

    public String getClientAuth() {
        return clientAuth;
    }

    public RpcClientProperties setClientAuth(String clientAuth) {
        this.clientAuth = clientAuth;
        return this;
    }

    public Boolean getEnableRegistry() {
        return enableRegistry;
    }

    public RpcClientProperties setEnableRegistry(Boolean enableRegistry) {
        this.enableRegistry = enableRegistry;
        return this;
    }

    public String getRegistrySchema() {
        return registrySchema;
    }

    public RpcClientProperties setRegistrySchema(String registrySchema) {
        this.registrySchema = registrySchema;
        return this;
    }

    public List<String> getRegistryAddress() {
        return registryAddress;
    }

    public RpcClientProperties setRegistryAddress(List<String> registryAddress) {
        this.registryAddress = registryAddress;
        return this;
    }

    public CompressType getCompressType() {
        return compressType;
    }

    public RpcClientProperties setCompressType(CompressType compressType) {
        this.compressType = compressType;
        return this;
    }

    public SerializeType getSerializeType() {
        return serializeType;
    }

    public RpcClientProperties setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
        return this;
    }
}
