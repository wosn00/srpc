package com.hex.netty.utils;

import java.util.UUID;

/**
 * @author: hs
 */
public class IdUtil {

    public static String getId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
