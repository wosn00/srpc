package com.hex.client;

import com.hex.thrift.PRCDataService;
import org.apache.thrift.TConfiguration;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author guohs
 * @date 2021/7/14
 */
public class ThriftClient {
    private static Logger logger = LoggerFactory.getLogger(ThriftClient.class);

    private PRCDataService.Client client;
    private TSocket transport;

    private int requestTimeout = 300000;
    private int connectTimeout = 5000;


    public void connect(String serverHost, int serverPort) {
        try {
            transport = new TSocket(new TConfiguration(), serverHost, serverPort, requestTimeout, connectTimeout);

            // 协议要和服务端一致
            TBinaryProtocol protocol = new TBinaryProtocol(transport);
            client = new PRCDataService.Client(protocol);
            transport.open();
        } catch (TException e) {
            logger.error("", e);
        }

    }

    public PRCDataService.Client getClient() {
        return client;
    }

    public void close() {
        // 关闭连接
        transport.close();
    }

    public static void main(String[] args) {
        ThriftClient thriftClient = new ThriftClient();
        thriftClient.connect("127.0.0.1", 8007);

        String result = null;
        try {
            result = thriftClient.getClient().getData("hex");
        } catch (TException e) {
            logger.error("客户端调用错误", e);
        } finally {
            // 实际应用中不要每次调用完就释放连接，可以做个连接管理器，初始化一定数量的连接，用完放回连接池中，实现复用连接
            thriftClient.close();
        }
        System.out.println(result);
    }


}
