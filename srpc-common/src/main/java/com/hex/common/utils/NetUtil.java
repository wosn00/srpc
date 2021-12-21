package com.hex.common.utils;

import com.hex.common.net.HostAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author guohs
 * @date 2021/9/9
 */
public class NetUtil {
    private static final Logger logger = LoggerFactory.getLogger(NetUtil.class);

    public static HostAndPort getLocalHostAndPort(int port) {
        HostAndPort local;
        try {
            local = HostAndPort.from(InetAddress.getLocalHost(), port);
        } catch (UnknownHostException e) {
            logger.error("get local hostAndPort failed");
            throw new RuntimeException();
        }
        return local;
    }

    public static void checkPort(Integer port) {
        if (port < 0 || port > 0xFFFF)
            throw new IllegalArgumentException("port out of range:" + port);
    }
}
