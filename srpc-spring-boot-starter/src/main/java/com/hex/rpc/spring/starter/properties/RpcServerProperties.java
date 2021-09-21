package com.hex.rpc.spring.starter.properties;

import com.hex.common.constant.RpcConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author: hs
 */
@ConfigurationProperties(prefix = "com.hex.srpc.server")
public class RpcServerProperties {
    private Integer port = 8008; //绑定端口
    private Integer channelWorkerThreads = RpcConstant.DEFAULT_THREADS; //channel处理工作线程数，连接数量多时可调大
    private Integer businessThreads = 0; //业务处理线程池，具有耗时业务时可配置，0为不设置

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

    private boolean compressEnable = true; //是否开启数据压缩（平均压缩率在60%以上，可节省大部分流量，性能损耗低）
    private Long minThreshold = -1L; //开启压缩包大小最低阈值(byte),超过则压缩,-1代表不限制
    private Long maxThreshold = -1L; //开启压缩包大小最高阈值(byte),低于则压缩,-1代表不限制

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

    public boolean isCompressEnable() {
        return compressEnable;
    }

    public RpcServerProperties setCompressEnable(boolean compressEnable) {
        this.compressEnable = compressEnable;
        return this;
    }

    public Long getMinThreshold() {
        return minThreshold;
    }

    public RpcServerProperties setMinThreshold(Long minThreshold) {
        this.minThreshold = minThreshold;
        return this;
    }

    public Long getMaxThreshold() {
        return maxThreshold;
    }

    public RpcServerProperties setMaxThreshold(Long maxThreshold) {
        this.maxThreshold = maxThreshold;
        return this;
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

    public Boolean getPreventDuplicateEnable() {
        return preventDuplicateEnable;
    }

    public void setPreventDuplicateEnable(Boolean preventDuplicateEnable) {
        this.preventDuplicateEnable = preventDuplicateEnable;
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
}
