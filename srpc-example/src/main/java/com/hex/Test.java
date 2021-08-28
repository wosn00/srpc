package com.hex;

import com.google.common.net.HostAndPort;

/**
 * @author: hs
 */
public class Test {

    public static void main(String[] args) {
        HostAndPort hostAndPort = HostAndPort.fromParts("127.0.0.1", 80000);
        System.out.println(hostAndPort.toString());
    }
}
