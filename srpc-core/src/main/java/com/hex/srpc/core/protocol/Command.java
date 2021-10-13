package com.hex.srpc.core.protocol;

import java.io.Serializable;

/**
 * @author hs
 */
public class Command implements Serializable {

    private static final long serialVersionUID = -5672014817638321964L;
    /**
     * 序号，指令唯一标识
     */
    private Long seq;

    /**
     * 指令头部信息，用于扩展
     */
    private String header;

    /**
     * 用于服务端映射对应的处理器
     */
    private String mapping;

    /**
     * 指令类型，请求或响应
     */
    private boolean isRequest;

    /**
     * 是否是心跳包
     */
    private boolean isHeartBeat;

    /**
     * 创建时间
     */
    private Long timestamp;

    public Command() {
    }

    public Command(Long seq, String header, String mapping, boolean isRequest, Long timestamp) {
        this.seq = seq;
        this.header = header;
        this.mapping = mapping;
        this.isRequest = isRequest;
        this.timestamp = timestamp;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public String getMapping() {
        return mapping;
    }

    public Command setMapping(String mapping) {
        this.mapping = mapping;
        return this;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public Command setRequest(boolean request) {
        isRequest = request;
        return this;
    }

    public boolean isHeartBeat() {
        return isHeartBeat;
    }

    public Command setHeartBeat(boolean heartBeat) {
        isHeartBeat = heartBeat;
        return this;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getHeader() {
        return header;
    }

    public Command setHeader(String header) {
        this.header = header;
        return this;
    }

    @Override
    public String toString() {
        return "Command{" +
                "seq=" + seq +
                ", header='" + header + '\'' +
                ", mapping='" + mapping + '\'' +
                ", isRequest=" + isRequest +
                ", isHeartBeat=" + isHeartBeat +
                ", timestamp=" + timestamp +
                '}';
    }
}
