package com.hex.netty.constant;

/**
 * @author: hs
 */
public enum CommandType {

    /**
     * 心跳指令
     */
    HEARTBEAT(0),

    /**
     * 请求指令
     */
    REQUEST_COMMAND(1),

    /**
     * 响应指令
     */
    RESPONSE_COMMAND(2);

    private Integer value;

    CommandType(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

}
