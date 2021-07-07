package com.hex.netty.chain.dealing;

import com.hex.netty.chain.Dealing;
import com.hex.netty.chain.DealingContext;
import com.hex.netty.cmd.IHandler;
import com.hex.netty.connection.Connection;
import com.hex.netty.constant.CommandType;
import com.hex.netty.invoke.ResponseFuture;
import com.hex.netty.invoke.ResponseMapping;
import com.hex.netty.protocol.Command;
import com.hex.netty.protocol.RpcRequest;
import com.hex.netty.protocol.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: hs
 * 分发处理器
 */
public class DispatchDealing implements Dealing {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 处理器映射
     */
    private Map<String, IHandler> handlerMap = new ConcurrentHashMap<>();

    @Override
    public void deal(DealingContext context) {
        Command command = context.getCommand();
        if (CommandType.HEARTBEAT.getValue().equals(command.getCommandType())) {
            // 链路心跳包处理
            logger.info("---connection receive a heartbeat packet");

        } else if (CommandType.REQUEST_COMMAND.getValue().equals(command.getCommandType())) {
            // 请求分发处理
            requestDispatch((RpcRequest) command, command.getCmd(), context.getConnectionManager().getConn());

        } else if (CommandType.RESPONSE_COMMAND.getValue().equals(command.getCommandType())) {
            // 响应处理
            responseProcess((RpcResponse) command);
        }
    }

    private void requestDispatch(RpcRequest rpcRequest, String cmd, Connection connection) {
        // 获取请求处理器
        IHandler iHandler = handlerMap.get(cmd);
        if (iHandler == null) {
            logger.warn("The handler of cmd is not defined, Ignore request seq:[{}]", rpcRequest.getSeq());
            return;
        }
        // 处理请求
        String responseBody;
        try {
            responseBody = iHandler.handler(rpcRequest);
        } catch (Exception e) {
            logger.error("handler processing error", e);
            connection.send(RpcResponse.serverError());
            return;
        }
        // 响应
        connection.send(RpcResponse.success(rpcRequest.getSeq(), responseBody));
    }

    private void responseProcess(RpcResponse rpcResponse) {
        ResponseFuture responseFuture = ResponseMapping.getResponseFuture(rpcResponse.getSeq());
        if (responseFuture == null) {
            // 获取不到，可能是服务端处理超时（2Min）
            logger.warn("Response mismatch request, maybe request already timeout");
            return;
        }
        // 设置响应内容，用于客户端获取
        responseFuture.setRpcResponse(rpcResponse);
        // 恢复客户端调用线程，并执行回调
        responseFuture.receipt();
    }

    public void registerHandler(IHandler handler) {
        handlerMap.put(handler.getCmd(), handler);
    }

    public DispatchDealing registerHandlers(List<IHandler> handlers) {
        Map<String, IHandler> map = handlers.stream().collect(Collectors.toMap(IHandler::getCmd, Function.identity()));
        handlerMap.putAll(map);
        return this;
    }
}
