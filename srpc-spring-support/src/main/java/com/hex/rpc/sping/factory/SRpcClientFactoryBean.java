package com.hex.rpc.sping.factory;

import com.hex.rpc.sping.reflect.SRpcInvocationHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Proxy;

/**
 * @author: hs
 */
public class SRpcClientFactoryBean implements FactoryBean, ApplicationContextAware {

    private Class<?> type;

    private ApplicationContext applicationContext;

    public SRpcClientFactoryBean setType(Class<?> type) {
        this.type = type;
        return this;
    }

    @Override
    public Object getObject() throws Exception {
        SRpcInvocationHandler invocationHandler = new SRpcInvocationHandler(applicationContext, type);
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{type}, invocationHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
