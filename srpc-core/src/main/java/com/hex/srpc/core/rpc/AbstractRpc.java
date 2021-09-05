package com.hex.srpc.core.rpc;

import com.hex.srpc.core.config.TLSConfig;
import io.netty.channel.epoll.Epoll;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.OpenSsl;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.traffic.GlobalTrafficShapingHandler;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author: hs
 */
public abstract class AbstractRpc {
    protected static final Logger logger = LoggerFactory.getLogger(AbstractRpc.class);

    private static final String CLASSPATH = "classpath:";

    protected GlobalTrafficShapingHandler trafficShapingHandler;

    protected SslContext sslContext;

    protected Thread shutdownHook;

    protected boolean useEpoll() {
        return SystemUtils.IS_OS_LINUX && Epoll.isAvailable();
    }

    protected void buildTrafficMonitor(ScheduledExecutorService executor, Boolean trafficMonitorEnable, Long maxReadSpeed, Long maxWriteSpeed) {
        if (trafficMonitorEnable != null && trafficMonitorEnable) {
            if (maxReadSpeed == null) {
                maxReadSpeed = 0L;
            }
            if (maxWriteSpeed == null) {
                maxWriteSpeed = 0L;
            }
            trafficShapingHandler = new GlobalTrafficShapingHandler(executor, maxWriteSpeed, maxReadSpeed);
        }
    }

    protected void buildSSLContext(boolean forClient, TLSConfig tlsConfig) throws Exception {
        InputStream keyIns = null;
        InputStream certIns = null;
        InputStream trustIns = null;
        try {
            keyIns = parseInputStream(tlsConfig.getKeyPath());
            certIns = parseInputStream(tlsConfig.getCertPath());
            SslContextBuilder sslContextBuilder;
            if (forClient) {
                sslContextBuilder = SslContextBuilder.forClient().keyManager(certIns, keyIns, tlsConfig.getKeyPwd());
            } else {
                sslContextBuilder = SslContextBuilder.forServer(certIns, keyIns, tlsConfig.getKeyPwd());
                sslContextBuilder.clientAuth(parseClientAuthMode(tlsConfig.getClientAuth()));
            }
            sslContextBuilder.sslProvider(sslProvider());

            if (tlsConfig.getTrustCertPath() == null || tlsConfig.getTrustCertPath().trim().isEmpty()) {
                sslContextBuilder.trustManager(InsecureTrustManagerFactory.INSTANCE);
            } else {
                trustIns = parseInputStream(tlsConfig.getTrustCertPath());
                sslContextBuilder.trustManager(trustIns);
            }
            sslContext = sslContextBuilder.build();
        } finally {
            if (certIns != null) {
                certIns.close();
            }
            if (keyIns != null) {
                keyIns.close();
            }
            if (trustIns != null) {
                trustIns.close();
            }
        }
    }

    protected void registerShutdownHook(Runnable runnable) {
        if (this.shutdownHook == null) {
            this.shutdownHook = new Thread("sRpcShutdownHook") {
                @Override
                public void run() {
                    runnable.run();
                }
            };
            Runtime.getRuntime().addShutdownHook(this.shutdownHook);
        }
    }

    private InputStream parseInputStream(String path) throws FileNotFoundException {
        if (path.startsWith(CLASSPATH)) {
            path = path.replaceFirst(CLASSPATH, "");
            return this.getClass().getClassLoader().getResourceAsStream(path);
        }
        return new FileInputStream(path);
    }

    protected ClientAuth parseClientAuthMode(String authMode) {
        if (authMode == null || authMode.trim().isEmpty()) {
            return ClientAuth.NONE;
        }
        for (ClientAuth clientAuth : ClientAuth.values()) {
            if (clientAuth.name().equals(authMode.toUpperCase())) {
                return clientAuth;
            }
        }
        return ClientAuth.NONE;
    }

    protected SslProvider sslProvider() {
        if (OpenSsl.isAvailable()) {
            return SslProvider.OPENSSL;
        } else {
            return SslProvider.JDK;
        }
    }


}
