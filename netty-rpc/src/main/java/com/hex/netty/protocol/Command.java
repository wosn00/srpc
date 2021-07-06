package com.hex.netty.protocol;

/**
 * @author hs
 */
public class Command<T> {

    /**
     * 序号，唯一标识
     */
    protected String seq;

    /**
     * 用于服务端分发请求，类似http url
     */
    protected String cmd;

    /**
     * 0为心跳，1为请求，2为响应
     */
    protected Integer commandType;

    /**
     * 响应码，只有响应才有
     */
    private Integer code;

    /**
     * 创建时间
     */
    protected Long ts;

    /**
     * 数据内容
     */
    protected T body;

    public Command() {
    }

    public Command(String seq, String cmd, Integer commandType, Integer code, Long ts, T body) {
        this.seq = seq;
        this.cmd = cmd;
        this.commandType = commandType;
        this.code = code;
        this.ts = ts;
        this.body = body;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Integer getCommandType() {
        return commandType;
    }

    public void setCommandType(Integer commandType) {
        this.commandType = commandType;
    }

    public Long getTs() {
        return ts;
    }

    public void setTs(Long ts) {
        this.ts = ts;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Command{" +
                "seq='" + seq + '\'' +
                ", cmd='" + cmd + '\'' +
                ", commandType=" + commandType +
                ", code=" + code +
                ", ts=" + ts +
                ", body=" + body +
                '}';
    }
}
