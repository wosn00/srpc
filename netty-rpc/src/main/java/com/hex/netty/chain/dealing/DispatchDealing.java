package com.hex.netty.chain.dealing;

import com.hex.netty.chain.Dealing;
import com.hex.netty.chain.DealingContext;
import com.hex.netty.connection.IConnection;
import com.hex.netty.constant.CommandType;
import com.hex.netty.constant.RpcConstant;
import com.hex.netty.exception.RpcException;
import com.hex.netty.invoke.ResponseFuture;
import com.hex.netty.invoke.ResponseMapping;
import com.hex.netty.protocol.Command;
import com.hex.netty.protocol.RpcRequest;
import com.hex.netty.protocol.RpcResponse;
import com.hex.netty.reflect.RouterFactory;
import com.hex.netty.reflect.RouterTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hs
 * 分发处理器
 */
public class DispatchDealing implements Dealing {
    private static final Logger logger = LoggerFactory.getLogger(DispatchDealing.class);

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
                heartBeatProcess(command, context.getConnection());
                break;
            case REQUEST_COMMAND:
                // 请求分发处理
                requestDispatch((RpcRequest) command, command.getCmd(), context.getConnection());
                break;
            case RESPONSE_COMMAND:
                // 响应处理
                responseProcess(command);
                break;
            default:
                logger.error("commandType:{} not support", type.getValue());
        }
    }

    private void requestDispatch(RpcRequest rpcRequest, String cmd, IConnection connection) {
        // 获取对应router
        RouterTarget target = RouterFactory.getRouter(cmd);
        if (target == null) {
            RpcResponse.serverError(rpcRequest.getSeq());
            return;
        }
        String result;
        try {
            result = target.invoke(rpcRequest);
        } catch (Exception e) {
            logger.error("error occurred on the RpcServer", e);
            connection.send(RpcResponse.serverError(rpcRequest.getSeq()));
            return;
        }
        // 响应
        connection.send(RpcResponse.success(rpcRequest.getSeq(), result));
    }

    private void responseProcess(Command rpcResponse) {
        ResponseFuture responseFuture = ResponseMapping.getResponseFuture(rpcResponse.getSeq());
        if (responseFuture == null) {
            // 获取不到，可能是服务端处理超时（30s）
            logger.warn("Response mismatch request, maybe request already timeout");
            return;
        }
        // 设置响应内容，用于客户端获取
        responseFuture.setRpcResponse(rpcResponse);
        // 恢复客户端调用线程，并执行回调
        responseFuture.receipt();
    }

    private void heartBeatProcess(Command<String> command, IConnection connection) {
        String body = command.getBody();
        if (RpcConstant.PING.equals(body)) {
            //服务端收到ping处理
            logger.info("[heartBeat]connection:{} receive a heartbeat packet from client", connection.getId());
            Command<String> pong = new Command<>();
            pong.setSeq(command.getSeq());
            pong.setCommandType(CommandType.HEARTBEAT.getValue());
            pong.setBody(RpcConstant.PONG);
            connection.send(pong);
        } else {
            //客户端收到pong处理
            responseProcess(command);
        }
    }
}
