package com.hex.netty.chain.dealing;

import com.hex.netty.chain.Dealing;
import com.hex.netty.chain.DealingContext;
import com.hex.netty.connection.Connection;
import com.hex.netty.constant.CommandType;
import com.hex.netty.invoke.ResponseFuture;
import com.hex.netty.invoke.ResponseMapping;
import com.hex.netty.protocol.Command;
import com.hex.netty.protocol.RpcRequest;
import com.hex.netty.protocol.RpcResponse;
import com.hex.netty.reflection.RouterFactory;
import com.hex.netty.reflection.RouterTarget;
import com.hex.netty.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: hs
 * 分发处理器
 */
public class DispatchDealing implements Dealing {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void deal(DealingContext context) {
        Command<String> command = context.getCommand();
        if (CommandType.HEARTBEAT.getValue().equals(command.getCommandType())) {
            // 链路心跳包处理
            heartBeatProcess(command, context.getConnection());

        } else if (CommandType.REQUEST_COMMAND.getValue().equals(command.getCommandType())) {
            // 请求分发处理
            requestDispatch((RpcRequest) command, command.getCmd(), context.getServerManager().getConn());

        } else if (CommandType.RESPONSE_COMMAND.getValue().equals(command.getCommandType())) {
            // 响应处理
            responseProcess((RpcResponse) command);
        }
    }

    private void requestDispatch(RpcRequest rpcRequest, String cmd, Connection connection) {
        // 获取对应router
        RouterTarget target = RouterFactory.getRouter(cmd);
        if (target == null) {
            RpcResponse.serverError(rpcRequest.getSeq());
            return;
        }
        String jsonResult;
        try {
            jsonResult = target.invoke(rpcRequest);
        } catch (Exception e) {
            logger.error("An error occurred on the RpcServer", e);
            connection.send(RpcResponse.serverError(rpcRequest.getSeq()));
            return;
        }
        // 响应
        connection.send(RpcResponse.success(rpcRequest.getSeq(), jsonResult));
    }

    private void responseProcess(RpcResponse rpcResponse) {
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

    private void heartBeatProcess(Command<String> command, Connection connection) {
        String body = command.getBody();
        if ("ping".equals(body)) {
            logger.info("-----connection:[{}] receive a heartbeat packet from client", connection.getId());
            Command<String> pong = new Command<>();
            pong.setSeq(Util.genSeq());
            pong.setCommandType(CommandType.HEARTBEAT.getValue());
            pong.setBody("pong");
            connection.send(pong);
        }
    }
}
