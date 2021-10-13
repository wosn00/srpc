package com.hex.common.utils;

/**
 * @author: hs
 */
public class ByteUtil {

    /**
     * 获取byte高四位
     *
     * @param data
     * @return
     */
    public static int getHeight4(int data) {
        int height;
        height = ((data & 0xf0) >> 4);
        return height;
    }

    /**
     * 获取byte低四位
     *
     * @param data
     * @return
     */
    public static int getLow4(int data) {
        int low;
        low = (data & 0x0f);
        return low;
    }

}
