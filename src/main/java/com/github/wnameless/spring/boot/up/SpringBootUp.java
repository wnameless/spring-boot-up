/*
 *
 * Copyright 2021 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.spring.boot.up;

import java.util.Map;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;

public final class SpringBootUp {

  private SpringBootUp() {
  }

  public static ApplicationContext applicationContext() {
    return ApplicationContextProvider.getApplicationContext();
  }

  public static <T> T getBean(String name, Class<T> requiredType) {
    return ApplicationContextProvider.getApplicationContext().getBean(name, requiredType);
  }

  public static Object getBean(String name) {
    return ApplicationContextProvider.getApplicationContext().getBean(name);
  }

  public static Object getBean(String name, Object... args) {
    return ApplicationContextProvider.getApplicationContext().getBean(name, args);
  }

  public static <T> T getBean(Class<T> requiredType) {
    return ApplicationContextProvider.getApplicationContext()
        .getBean(requiredType);
  }

  public static <T> T getBean(Class<T> requiredType, Object... args) {
    return ApplicationContextProvider.getApplicationContext().getBean(requiredType, args);
  }

  public static <T> Map<String, T> getBeansOfType(Class<T> type) {
    return ApplicationContextProvider.getApplicationContext()
        .getBeansOfType(type);
  }

  public static <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) {
    return ApplicationContextProvider.getApplicationContext()
        .getBeansOfType(type, includeNonSingletons, allowEagerInit);
  }

  public static AutowireCapableBeanFactory autowireCapableBeanFactory() {
    return ApplicationContextProvider.getApplicationContext().getAutowireCapableBeanFactory();
  }

  public static <T> T autowire(Class<T> beanClass, int autowireMode, boolean dependencyCheck) {
    return beanClass.cast(ApplicationContextProvider.getApplicationContext().getAutowireCapableBeanFactory()
        .autowire(beanClass, autowireMode, dependencyCheck));
  }

  public static <T> T autowireBean(T existingBean) {
    ApplicationContextProvider.getApplicationContext()
        .getAutowireCapableBeanFactory().autowireBean(existingBean);
    return existingBean;
  }

  public static <T> T autowireBeanProperties(T existingBean, int autowireMode, boolean dependencyCheck) {
    ApplicationContextProvider.getApplicationContext()
        .getAutowireCapableBeanFactory().autowireBeanProperties(existingBean, autowireMode, dependencyCheck);
    return existingBean;
  }

}