package com.hex.server;


import com.hex.proto.TestRequest;
import com.hex.proto.TestResponse;
import com.hex.proto.TestRpcGrpc;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author guohs
 * @date 2021/7/14
 */
public class RpcServerImpl extends TestRpcGrpc.TestRpcImplBase {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public void getData(TestRequest request, StreamObserver<TestResponse> responseObserver) {
        TestResponse response = null;
        try {
            String name = request.getName();
            System.out.println("收到客户端调用:" + name);
            response = TestResponse.newBuilder()
                    .setMessage("hello" + name)
                    .build();
        } catch (Exception e) {
            logger.error("", e);
            responseObserver.onError(e);
        } finally {
            responseObserver.onNext(response);
        }
        responseObserver.onCompleted();

    }


}
