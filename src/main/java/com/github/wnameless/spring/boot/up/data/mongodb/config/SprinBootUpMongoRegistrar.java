package com.github.wnameless.spring.boot.up.data.mongodb.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class SprinBootUpMongoRegistrar implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
    String simpleName = SpringBootUpMongoEventListener.class.getName();
    RootBeanDefinition rootBeanDefinition = new RootBeanDefinition();
    rootBeanDefinition.setBeanClass(SpringBootUpMongoEventListener.class);
    registry.registerBeanDefinition(simpleName, rootBeanDefinition);
  }

}
