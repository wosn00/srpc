package com.hex.netty.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.UUID;

/**
 * @author: hs
 */
public class Util {
    public static String genSeq() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static String jsonSerializePretty(Object o) {
        return JSON.toJSONString(o, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.PrettyFormat);
    }
}
