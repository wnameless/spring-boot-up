package com.github.wnameless.spring.boot.up.data.mongodb.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import com.github.wnameless.spring.boot.up.data.mongodb.cascade.CascadeMongoEventListener;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.InterceptorMongoEventListener;

public class SprinBootUpMongoRegistrar implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
    RootBeanDefinition cascadeMongoEventBean = new RootBeanDefinition();
    cascadeMongoEventBean.setBeanClass(CascadeMongoEventListener.class);
    registry.registerBeanDefinition(CascadeMongoEventListener.class.getName(),
        cascadeMongoEventBean);

    RootBeanDefinition beforeAndAfterMongoEventBean = new RootBeanDefinition();
    beforeAndAfterMongoEventBean.setBeanClass(InterceptorMongoEventListener.class);
    registry.registerBeanDefinition(InterceptorMongoEventListener.class.getName(),
        beforeAndAfterMongoEventBean);
  }

}
