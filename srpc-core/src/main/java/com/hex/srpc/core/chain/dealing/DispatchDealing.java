package com.hex.srpc.core.chain.dealing;

import com.hex.common.utils.SerializerUtil;
import com.hex.srpc.core.chain.Dealing;
import com.hex.srpc.core.chain.DealingContext;
import com.hex.srpc.core.connection.IConnection;
import com.hex.common.constant.CommandType;
import com.hex.common.constant.RpcConstant;
import com.hex.common.exception.RpcException;
import com.hex.srpc.core.invoke.ResponseFuture;
import com.hex.srpc.core.invoke.ResponseMapping;
import com.hex.srpc.core.protocol.Command;
import com.hex.srpc.core.protocol.RpcRequest;
import com.hex.srpc.core.protocol.RpcResponse;
import com.hex.srpc.core.reflect.RouterFactory;
import com.hex.srpc.core.reflect.RouterTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hs
 * 分发处理器
 */
public class DispatchDealing implements Dealing {
    private static final Logger logger = LoggerFactory.getLogger(DispatchDealing.class);

    private ResponseMapping responseMapping;

    public DispatchDealing() {
    }

    public DispatchDealing(ResponseMapping responseMapping) {
        this.responseMapping = responseMapping;
    }

    @Override
    public void deal(DealingContext context) {
        Command<String> command = context.getCommand();
        if (command.getCommandType() == null) {
            throw new RpcException("commandType is null");
        }
        CommandType type = CommandType.getType(command.getCommandType());

        switch (type) {
            case HEARTBEAT:
                // 链路心跳包处理
                heartBeatProcess(command, context);
                break;
            case REQUEST_COMMAND:
                // 请求分发处理
                requestDispatch((RpcRequest<String>) command, command.getCmd(), context.getConnection());
                break;
            case RESPONSE_COMMAND:
                // 响应处理
                responseProcess(command);
                break;
            default:
                logger.error("commandType:{} not support", type.getValue());
                throw new RpcException();
        }
    }

    private void requestDispatch(RpcRequest<String> rpcRequest, String cmd, IConnection connection) {
        // 获取对应router
        RouterTarget target = RouterFactory.getRouter(cmd);
        if (target == null) {
            RpcResponse.serverError(rpcRequest.getSeq());
            return;
        }
        Object result;
        try {
            result = target.invoke(rpcRequest);
        } catch (Exception e) {
            logger.error("error occurred on the RpcServer", e);
            connection.send(RpcResponse.serverError(rpcRequest.getSeq()));
            return;
        }
        // 响应
        connection.send(RpcResponse.success(rpcRequest.getSeq(), rpcRequest.getCmd(), result));
    }

    private void responseProcess(Command<String> rpcResponse) {
        ResponseFuture responseFuture = responseMapping.getResponseFuture(rpcResponse.getSeq());
        if (responseFuture == null) {
            // 获取不到，可能是服务端处理超时
            if (logger.isWarnEnabled()) {
                logger.warn("Response mismatch request, response : {}", SerializerUtil.serializePretty(rpcResponse));
            }
            return;
        }
        // 设置响应内容，用于客户端获取
        responseFuture.setRpcResponse(rpcResponse);
        // 恢复客户端调用线程，并执行回调
        responseFuture.receipt();
    }

    private void heartBeatProcess(Command<String> command, DealingContext context) {
        String body = SerializerUtil.deserialize(command.getBody(), String.class);
        if (RpcConstant.PING.equals(body)) {
            //服务端收到ping处理
            if (context.isPrintHeartbeatInfo()) {
                logger.info("[heartBeat]connection:{} receive a heartbeat packet from client", context.getConnection().getId());
            }
            Command<String> pong = new Command<>();
            pong.setSeq(command.getSeq());
            pong.setCommandType(CommandType.HEARTBEAT.getValue());
            pong.setBody(RpcConstant.PONG);
            context.getConnection().send(pong);
        } else {
            //客户端收到pong处理
            responseProcess(command);
        }
    }
}
