package com.github.wnameless.spring.boot.up.data.mongodb.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import com.github.wnameless.spring.boot.up.data.mongodb.cascade.CascadeMongoEventListener;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.InterceptorMongoEventListener;

public class SprinBootUpMongoRegistrar implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
    GenericBeanDefinition cascadeMongoEventBean = new GenericBeanDefinition();
    cascadeMongoEventBean.setBeanClass(CascadeMongoEventListener.class);
    registry.registerBeanDefinition(
        importBeanNameGenerator.generateBeanName(cascadeMongoEventBean, registry),
        cascadeMongoEventBean);

    GenericBeanDefinition interceptorMongoEventBean = new GenericBeanDefinition();
    interceptorMongoEventBean.setBeanClass(InterceptorMongoEventListener.class);
    registry.registerBeanDefinition(
        importBeanNameGenerator.generateBeanName(interceptorMongoEventBean, registry),
        interceptorMongoEventBean);
  }

}
