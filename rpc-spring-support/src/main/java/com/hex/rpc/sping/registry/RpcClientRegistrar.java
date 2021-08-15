package com.hex.rpc.sping.registry;

import com.hex.rpc.sping.annotation.EnableRpc;
import com.hex.rpc.sping.annotation.RpcClient;
import com.hex.rpc.sping.factory.RpcClientFactoryBean;
import com.hex.rpc.sping.scanner.RpcClientScanner;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author: hs
 */
public class RpcClientRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(RpcClientRegistrar.class);

    private ResourceLoader resourceLoader;

    private Environment environment;

    private static final String BASE_PACKAGES = "basePackages";

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        //获取需要扫描的包路径
        Set<String> basePackages = new HashSet<>();
        Map<String, Object> attributes = metadata
                .getAnnotationAttributes(EnableRpc.class.getName());
        if (attributes != null && attributes.get(BASE_PACKAGES) != null) {
            String[] packages = (String[]) attributes.get(BASE_PACKAGES);
            if (packages != null && packages.length > 0) {
                for (String aPackage : packages) {
                    if (StringUtils.isNotBlank(aPackage)) {
                        basePackages.add(aPackage);
                    }
                }
            }
        }
        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }
        //扫描并注册
        doScanAndRegister(basePackages, registry);
    }

    private void doScanAndRegister(Set<String> basePackages, BeanDefinitionRegistry registry) {
        RpcClientScanner scanner = new RpcClientScanner(false, environment);
        scanner.setResourceLoader(resourceLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(RpcClient.class);
        scanner.addIncludeFilter(annotationTypeFilter);

        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition candidateComponent : candidateComponents) {
                if (candidateComponent instanceof AnnotatedBeanDefinition) {
                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                    Assert.isTrue(annotationMetadata.isInterface(),
                            "@RpcClient can only be specified on an interface");
                    //注册server address
                    RpcServerAddressRegistry.register(annotationMetadata);
                    //注册bean
                    registerClient(registry, annotationMetadata);
                }
            }
        }
    }

    private void registerClient(BeanDefinitionRegistry registry, AnnotationMetadata annotationMetadata) {
        String className = annotationMetadata.getClassName();
        BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(RpcClientFactoryBean.class);
        definition.addPropertyValue("type", className);
        definition.setRole(RootBeanDefinition.ROLE_INFRASTRUCTURE);
        BeanDefinitionHolder holder = new BeanDefinitionHolder(definition.getBeanDefinition(), className);
        // 注册到spring容器
        BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);

    }
}
