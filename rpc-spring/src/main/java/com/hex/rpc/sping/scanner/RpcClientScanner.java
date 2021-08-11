package com.hex.rpc.sping.scanner;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;

/**
 * @author guohs
 * @date 2021/8/11
 */
public class RpcClientScanner extends ClassPathScanningCandidateComponentProvider {

    public RpcClientScanner(boolean useDefaultFilters, Environment environment) {
        super(useDefaultFilters, environment);
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isIndependent() &&
                beanDefinition.getMetadata().isInterface();
    }
}
