package com.hex.server;

import com.hex.thrift.PRCDataService;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author guohs
 * @date 2021/7/14
 */
public class ThriftServer {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private int port = 8007;

    private RPCDataServiceImpl rpcDataService = new RPCDataServiceImpl();

    public void start() {
        PRCDataService.Processor processor = new PRCDataService.Processor<PRCDataService.Iface>(rpcDataService);
        TBinaryProtocol.Factory protocolFactory = new TBinaryProtocol.Factory();
        TTransportFactory transportFactory = new TTransportFactory();
        try {
            TServerTransport transport = new TServerSocket(port);
            TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(transport);
            tArgs.processor(processor);
            tArgs.protocolFactory(protocolFactory);
            tArgs.transportFactory(transportFactory);
            tArgs.minWorkerThreads(5);
            tArgs.maxWorkerThreads(5);
            TServer server = new TThreadPoolServer(tArgs);
            logger.info("thrift服务启动成功, 端口={}", port);
            server.serve();
        } catch (Exception e) {
            logger.error("thrift服务启动失败", e);
        }
    }

    public static void main(String[] args) {
        // 启动服务端
        new ThriftServer().start();
    }
}
