package com.hex.rpc.spring.starter.properties;

import com.hex.common.constant.CompressType;
import com.hex.common.constant.RpcConstant;
import com.hex.common.constant.SerializeType;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author: hs
 */
@ConfigurationProperties(prefix = "srpc.server")
public class RpcServerProperties {
    private Integer port = 9957; //绑定端口

    private Integer channelWorkerThreads = RpcConstant.DEFAULT_THREADS; //channel处理工作线程数，连接数量多时可调大

    private Integer businessThreads = 200; //业务处理线程池，0为不设置
    private Integer businessQueueSize = 500; //业务线程池队列大小

    private Integer connectionIdleTime = 180;//超过连接空闲时间(秒)未收发数据则关闭连接
    private Integer printConnectionNumInterval = 0; //打印服务端当前连接数, 时间间隔(秒), 0为不打印
    private Boolean isPrintHearBeatPacketInfo = false; //是否打印心跳包信息

    private CompressType compressType = CompressType.SNAPPY; //压缩算法类型，无需压缩为NONE
    private SerializeType serializeType = SerializeType.PROTOSTUFF; //序列化类型，默认protostuff

    private Integer sendBuf = 65535; //tcp发送缓冲区
    private Integer receiveBuf = 65535; //tcp接收缓冲区
    private Integer lowWaterLevel = 1024 * 1024; //低水位
    private Integer highWaterLevel = 10 * 1024 * 1024; //高水位

    private boolean deDuplicateEnable = false; //是否开启去重处理
    private Integer duplicateCheckTime = 10; //请求去重缓存时长(秒)
    private Long duplicateMaxSize = 1024 * 64L; //最大缓存请求个数

    private Boolean trafficMonitorEnable = false; //是否开启流控
    private Long maxReadSpeed = 10 * 1000 * 1000L; //带宽限制，最大读取速度
    private Long maxWriteSpeed = 10 * 1000 * 1000L; //带宽限制，最大写出速度

    // tls加密部分配置
    private Boolean useTLS = false; //是否开启tls加密
    private String keyPath; //私钥文件路径
    private String keyPwd; //密码
    private String certPath; //证书文件路径
    private String trustCertPath; //受信任ca证书路径
    private String clientAuth; //是否要求客户端认证

    // 注册中心配置部分
    private Boolean enableRegistry = false; //是否使用注册中心
    private String registrySchema; //注册中心模式名称
    private List<String> registryAddress; //注册中心地址

    public Integer getChannelWorkerThreads() {
        return channelWorkerThreads;
    }

    public RpcServerProperties setChannelWorkerThreads(Integer channelWorkerThreads) {
        this.channelWorkerThreads = channelWorkerThreads;
        return this;
    }

    public Integer getBusinessThreads() {
        return businessThreads;
    }

    public RpcServerProperties setBusinessThreads(Integer businessThreads) {
        this.businessThreads = businessThreads;
        return this;
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


    public Boolean getTrafficMonitorEnable() {
        return trafficMonitorEnable;
    }

    public RpcServerProperties setTrafficMonitorEnable(Boolean trafficMonitorEnable) {
        this.trafficMonitorEnable = trafficMonitorEnable;
        return this;
    }

    public Long getMaxReadSpeed() {
        return maxReadSpeed;
    }

    public RpcServerProperties setMaxReadSpeed(Long maxReadSpeed) {
        this.maxReadSpeed = maxReadSpeed;
        return this;
    }

    public Long getMaxWriteSpeed() {
        return maxWriteSpeed;
    }

    public RpcServerProperties setMaxWriteSpeed(Long maxWriteSpeed) {
        this.maxWriteSpeed = maxWriteSpeed;
        return this;
    }

    public boolean isDeDuplicateEnable() {
        return deDuplicateEnable;
    }

    public RpcServerProperties setDeDuplicateEnable(boolean deDuplicateEnable) {
        this.deDuplicateEnable = deDuplicateEnable;
        return this;
    }

    public Integer getDuplicateCheckTime() {
        return duplicateCheckTime;
    }

    public RpcServerProperties setDuplicateCheckTime(Integer duplicateCheckTime) {
        this.duplicateCheckTime = duplicateCheckTime;
        return this;
    }

    public Long getDuplicateMaxSize() {
        return duplicateMaxSize;
    }

    public RpcServerProperties setDuplicateMaxSize(Long duplicateMaxSize) {
        this.duplicateMaxSize = duplicateMaxSize;
        return this;
    }

    public Integer getConnectionIdleTime() {
        return connectionIdleTime;
    }

    public RpcServerProperties setConnectionIdleTime(Integer connectionIdleTime) {
        this.connectionIdleTime = connectionIdleTime;
        return this;
    }

    public Integer getPrintConnectionNumInterval() {
        return printConnectionNumInterval;
    }

    public RpcServerProperties setPrintConnectionNumInterval(Integer printConnectionNumInterval) {
        this.printConnectionNumInterval = printConnectionNumInterval;
        return this;
    }

    public Boolean getUseTLS() {
        return useTLS;
    }

    public RpcServerProperties setUseTLS(Boolean useTLS) {
        this.useTLS = useTLS;
        return this;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public RpcServerProperties setKeyPath(String keyPath) {
        this.keyPath = keyPath;
        return this;
    }

    public String getKeyPwd() {
        return keyPwd;
    }

    public RpcServerProperties setKeyPwd(String keyPwd) {
        this.keyPwd = keyPwd;
        return this;
    }

    public String getCertPath() {
        return certPath;
    }

    public RpcServerProperties setCertPath(String certPath) {
        this.certPath = certPath;
        return this;
    }

    public String getTrustCertPath() {
        return trustCertPath;
    }

    public RpcServerProperties setTrustCertPath(String trustCertPath) {
        this.trustCertPath = trustCertPath;
        return this;
    }

    public String getClientAuth() {
        return clientAuth;
    }

    public RpcServerProperties setClientAuth(String clientAuth) {
        this.clientAuth = clientAuth;
        return this;
    }

    public Boolean getEnableRegistry() {
        return enableRegistry;
    }

    public RpcServerProperties setEnableRegistry(Boolean enableRegistry) {
        this.enableRegistry = enableRegistry;
        return this;
    }

    public String getRegistrySchema() {
        return registrySchema;
    }

    public RpcServerProperties setRegistrySchema(String registrySchema) {
        this.registrySchema = registrySchema;
        return this;
    }

    public List<String> getRegistryAddress() {
        return registryAddress;
    }

    public RpcServerProperties setRegistryAddress(List<String> registryAddress) {
        this.registryAddress = registryAddress;
        return this;
    }

    public Integer getBusinessQueueSize() {
        return businessQueueSize;
    }

    public RpcServerProperties setBusinessQueueSize(Integer businessQueueSize) {
        this.businessQueueSize = businessQueueSize;
        return this;
    }

    public CompressType getCompressType() {
        return compressType;
    }

    public RpcServerProperties setCompressType(CompressType compressType) {
        this.compressType = compressType;
        return this;
    }

    public SerializeType getSerializeType() {
        return serializeType;
    }

    public RpcServerProperties setSerializeType(SerializeType serializeType) {
        this.serializeType = serializeType;
        return this;
    }

    public Boolean getPrintHearBeatPacketInfo() {
        return isPrintHearBeatPacketInfo;
    }

    public RpcServerProperties setPrintHearBeatPacketInfo(Boolean printHearBeatPacketInfo) {
        isPrintHearBeatPacketInfo = printHearBeatPacketInfo;
        return this;
    }
}
