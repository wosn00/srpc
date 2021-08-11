package com.hex.rpc.sping.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author: hs
 */
public class RpcClientFactoryBean<T> implements FactoryBean<T>, ApplicationContextAware {

    private Class<T> type;

    private ApplicationContext applicationContext;

    public RpcClientFactoryBean setType(Class<T> type) {
        this.type = type;
        return this;
    }

    @Override
    public T getObject() throws Exception {

        return null;
    }

    @Override
    public Class<T> getObjectType() {
        return type;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
