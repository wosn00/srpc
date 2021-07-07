package com.hex.netty.util;

import java.util.UUID;

/**
 * @author: hs
 */
public class Util {
    public static String genSeq() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
