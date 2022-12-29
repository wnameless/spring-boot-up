package com.github.wnameless.spring.boot.up.data.mongodb.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import com.github.wnameless.spring.boot.up.data.mongodb.entity.CascadeMongoEventListener;
import com.github.wnameless.spring.boot.up.data.mongodb.event.BeforeAndAfterMongoEventListener;

public class SprinBootUpMongoRegistrar implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
    RootBeanDefinition cascadeMongoEventBean = new RootBeanDefinition();
    cascadeMongoEventBean.setBeanClass(CascadeMongoEventListener.class);
    registry.registerBeanDefinition(CascadeMongoEventListener.class.getName(),
        cascadeMongoEventBean);

    RootBeanDefinition beforeAndAfterMongoEventBean = new RootBeanDefinition();
    beforeAndAfterMongoEventBean.setBeanClass(BeforeAndAfterMongoEventListener.class);
    registry.registerBeanDefinition(BeforeAndAfterMongoEventListener.class.getName(),
        beforeAndAfterMongoEventBean);
  }

}
