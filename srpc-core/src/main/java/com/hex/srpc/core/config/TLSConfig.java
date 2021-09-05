package com.hex.srpc.core.config;

/**
 * @author: hs
 */
public class TLSConfig {

    private Boolean useTLS; // 是否开启tls加密
    private String keyPath; //私钥
    private String keyPwd; //密码
    private String certPath; //证书路径
    private String trustCertPath; //受信任ca证书路径
    private String clientAuth; //模式

    public Boolean getUseTLS() {
        return useTLS;
    }

    public TLSConfig setUseTLS(Boolean useTLS) {
        this.useTLS = useTLS;
        return this;
    }

    public String getKeyPath() {
        return keyPath;
    }

    public TLSConfig setKeyPath(String keyPath) {
        this.keyPath = keyPath;
        return this;
    }

    public String getKeyPwd() {
        return keyPwd;
    }

    public TLSConfig setKeyPwd(String keyPwd) {
        this.keyPwd = keyPwd;
        return this;
    }

    public String getCertPath() {
        return certPath;
    }

    public TLSConfig setCertPath(String certPath) {
        this.certPath = certPath;
        return this;
    }

    public String getTrustCertPath() {
        return trustCertPath;
    }

    public TLSConfig setTrustCertPath(String trustCertPath) {
        this.trustCertPath = trustCertPath;
        return this;
    }

    public String getClientAuth() {
        return clientAuth;
    }

    public TLSConfig setClientAuth(String clientAuth) {
        this.clientAuth = clientAuth;
        return this;
    }
}
