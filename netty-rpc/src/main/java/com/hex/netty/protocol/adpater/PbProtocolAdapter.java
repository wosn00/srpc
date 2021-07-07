package com.hex.netty.protocol.adpater;

import com.hex.netty.constant.CommandType;
import com.hex.netty.exception.RpcException;
import com.hex.netty.protocol.Command;
import com.hex.netty.protocol.RpcRequest;
import com.hex.netty.protocol.RpcResponse;
import com.hex.netty.protocol.pb.proto.Rpc;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: hs
 */
public class PbProtocolAdapter implements ProtocolAdapter<Command<String>, Rpc.Packet> {

    private volatile static PbProtocolAdapter adapter;

    private PbProtocolAdapter() {
    }

    public static PbProtocolAdapter getAdapter() {
        if (adapter == null) {
            synchronized (PbProtocolAdapter.class) {
                if (adapter == null) {
                    adapter = new PbProtocolAdapter();
                }
            }
        }
        return adapter;
    }

    @Override
    public Rpc.Packet encode(Command command) {
        Rpc.Packet.Builder builder = Rpc.Packet.newBuilder();
        if (StringUtils.isBlank(command.getSeq())) {
            throw new RpcException("seq can not be null!");
        }
        builder.setSeq(command.getSeq());
        if (StringUtils.isNotBlank(command.getCmd())) {
            builder.setCmd(command.getCmd());
        }
        if (command.getCode() != null) {
            builder.setCode(command.getCode());
        }
        builder.setCommandType(command.getCommandType());
        String body = (String) command.getBody();
        if (command.getTs() == null) {
            builder.setTs(System.currentTimeMillis());
        } else {
            builder.setTs(command.getTs());
        }
        if (StringUtils.isNotBlank(body)) {
            builder.setBody(body);
        }
        return builder.build();
    }

    @Override
    public Command<String> decode(Rpc.Packet packet) {
        Command<String> command;
        if (CommandType.REQUEST_COMMAND.getValue().equals(packet.getCommandType())) {
            command = new RpcRequest();
        } else if (CommandType.RESPONSE_COMMAND.getValue().equals(packet.getCommandType())) {
            command = new RpcResponse();
        } else {
            command = new Command<>();
        }
        command.setSeq(packet.getSeq());
        command.setCmd(packet.getCmd());
        command.setCommandType(packet.getCommandType());
        command.setTs(packet.getTs());
        if (packet.getCode() > 0) {
            command.setCode(packet.getCode());
        }
        if (StringUtils.isNotBlank(packet.getBody())) {
            command.setBody(packet.getBody());
        }
        return command;
    }
}
