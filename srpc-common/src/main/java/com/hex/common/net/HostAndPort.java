package com.hex.common.net;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author: hs
 */
public class HostAndPort {

    private String host;

    private Integer port;

    public HostAndPort(String host, Integer port) {
        this.host = host;
        this.port = port;
    }

    public static HostAndPort from(InetSocketAddress socketAddress) {
        return new HostAndPort(socketAddress.getHostString(), socketAddress.getPort());
    }

    public static HostAndPort from(String address) {
        if (StringUtils.isNotBlank(address)) {
            String[] split = address.split(":");
            return new HostAndPort(split[0], Integer.valueOf(split[1]));
        }
        return null;
    }

    public static HostAndPort from(InetAddress address, int port) {
        return new HostAndPort(address.getHostAddress(), port);
    }

    public String getHost() {
        return host;
    }

    public HostAndPort setHost(String host) {
        this.host = host;
        return this;
    }

    public Integer getPort() {
        return port;
    }

    public HostAndPort setPort(Integer port) {
        this.port = port;
        return this;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HostAndPort that = (HostAndPort) o;
        return host.equals(that.host) &&
                port.equals(that.port);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }
}
