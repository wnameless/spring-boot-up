package com.github.wnameless.spring.boot.up.jsf.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.thymeleaf.dialect.springdata.SpringDataDialect;
import com.github.wnameless.spring.boot.up.thymeleaf.AjaxDialect;
import com.github.wnameless.spring.boot.up.thymeleaf.HtmxDialect;

public class SprinBootUpJSFRegistrar implements ImportBeanDefinitionRegistrar {

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
      BeanDefinitionRegistry registry, BeanNameGenerator importBeanNameGenerator) {
    GenericBeanDefinition htmxDialectBean = new GenericBeanDefinition();
    htmxDialectBean.setBeanClass(HtmxDialect.class);
    registry.registerBeanDefinition(
        importBeanNameGenerator.generateBeanName(htmxDialectBean, registry), htmxDialectBean);

    GenericBeanDefinition ajaxDialectBean = new GenericBeanDefinition();
    ajaxDialectBean.setBeanClass(AjaxDialect.class);
    registry.registerBeanDefinition(
        importBeanNameGenerator.generateBeanName(ajaxDialectBean, registry), ajaxDialectBean);

    GenericBeanDefinition springDataDialectBean = new GenericBeanDefinition();
    springDataDialectBean.setBeanClass(SpringDataDialect.class);
    registry.registerBeanDefinition(
        importBeanNameGenerator.generateBeanName(springDataDialectBean, registry),
        springDataDialectBean);
  }

}
