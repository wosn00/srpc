package com.hex.server;

import com.hex.thrift.PRCDataService;
import org.apache.thrift.TException;

/**
 * @author guohs
 * @date 2021/7/14
 */
public class RPCDataServiceImpl implements PRCDataService.Iface {
    @Override
    public String getData(String userName) throws TException {
        System.out.println("服务端收到调用：" + userName);
        return "hello" + userName;
    }
}
