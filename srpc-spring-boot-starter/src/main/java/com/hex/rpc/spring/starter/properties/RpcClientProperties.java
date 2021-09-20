package com.hex.rpc.spring.starter.properties;

import com.hex.common.constant.LoadBalanceRule;
import com.hex.common.constant.RpcConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author: hs
 */
@ConfigurationProperties(prefix = "com.hex.srpc.client")
public class RpcClientProperties {
    private Integer selectorThreads = RpcConstant.DEFAULT_THREADS; //io线程数
    private Integer workerThreads = RpcConstant.DEFAULT_THREADS; //工作线程数

    private Integer connectionTimeout = 5; //连接超时时间(秒)
    private Integer requestTimeout = 10; //请求超时时间(秒)
    private Integer connectionSizePerNode = 5; //每个节点连接数
    private Integer connectionIdleTime = 180; //超过连接空闲时间(秒)未收发数据则关闭连接
    private Integer heartBeatTimeInterval = 30; //发送心跳包间隔时间(秒)

    private LoadBalanceRule loadBalanceRule = LoadBalanceRule.RANDOM; //集群负载均衡策略
    private boolean excludeUnAvailableNodesEnable = true; //集群模式下是否排除不可用的节点
    private Integer nodeErrorTimes = 3; //节点连接或请求超时/异常超过设置次数则置为节点不可用
    private Integer nodeHealthCheckTimeInterval = 10; //节点健康检查周期(秒),心跳包响应成功则恢复不可用的节点

    private Integer sendBuf = 65535; //tcp发送缓冲区
    private Integer receiveBuf = 65535; //tcp接收缓冲区
    private Integer lowWaterLevel = 1024 * 1024; //低水位
    private Integer highWaterLevel = 10 * 1024 * 1024; //高水位

    private boolean deDuplicateEnable = true; //是否开启去重处理
    private Integer duplicateCheckTime = 10; //请求去重缓存时长(秒)
    private Long duplicateMaxSize = 1024 * 64L; //最大缓存请求个数

    private Boolean trafficMonitorEnable = true; //是否开启流量控制
    private Long maxReadSpeed = 10 * 1000 * 1000L; //带宽限制，最大读取速度
    private Long maxWriteSpeed = 10 * 1000 * 1000L; //带宽限制，最大写出速度

    private boolean compressEnable = true; //是否开启数据压缩（平均压缩率在60%以上，可节省大部分流量，性能损耗低）
    private Long minThreshold = -1L; //开启压缩包大小最低阈值(byte),超过则压缩,-1代表不限制
    private Long maxThreshold = -1L; //开启压缩包大小最高阈值(byte),低于则压缩,-1代表不限制

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

    public Integer getSelectorThreads() {
        return selectorThreads;
    }

    public RpcClientProperties setSelectorThreads(Integer selectorThreads) {
        this.selectorThreads = selectorThreads;
        return this;
    }

    public Integer getWorkerThreads() {
        return workerThreads;
    }

    public RpcClientProperties setWorkerThreads(Integer workerThreads) {
        this.workerThreads = workerThreads;
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

    public boolean isDeDuplicateEnable() {
        return deDuplicateEnable;
    }

    public RpcClientProperties setDeDuplicateEnable(boolean deDuplicateEnable) {
        this.deDuplicateEnable = deDuplicateEnable;
        return this;
    }

    public Integer getDuplicateCheckTime() {
        return duplicateCheckTime;
    }

    public RpcClientProperties setDuplicateCheckTime(Integer duplicateCheckTime) {
        this.duplicateCheckTime = duplicateCheckTime;
        return this;
    }

    public Long getDuplicateMaxSize() {
        return duplicateMaxSize;
    }

    public RpcClientProperties setDuplicateMaxSize(Long duplicateMaxSize) {
        this.duplicateMaxSize = duplicateMaxSize;
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

    public boolean isCompressEnable() {
        return compressEnable;
    }

    public RpcClientProperties setCompressEnable(boolean compressEnable) {
        this.compressEnable = compressEnable;
        return this;
    }

    public Long getMinThreshold() {
        return minThreshold;
    }

    public RpcClientProperties setMinThreshold(Long minThreshold) {
        this.minThreshold = minThreshold;
        return this;
    }

    public Long getMaxThreshold() {
        return maxThreshold;
    }

    public RpcClientProperties setMaxThreshold(Long maxThreshold) {
        this.maxThreshold = maxThreshold;
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
}
