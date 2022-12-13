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
import java.util.Optional;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * {@link SpringBootUp} is an utility class and a major entry point for SpringBootUp library.
 * 
 */
public final class SpringBootUp {

  private SpringBootUp() {}

  /**
   * Retuens the Spring {@link ApplicationContext}.
   * 
   * @return a Spring {@link ApplicationContext}
   */
  public static ApplicationContext applicationContext() {
    return ApplicationContextProvider.getApplicationContext();
  }

  /**
   * {@link BeanFactory#getBean}
   * 
   * @param <T> the type of a bean
   * @param name of a bean
   * @param requiredType of a bean
   * @return a bean instance
   */
  public static <T> T getBean(String name, Class<T> requiredType) {
    return applicationContext().getBean(name, requiredType);
  }

  public static Object getBean(String name) {
    return applicationContext().getBean(name);
  }

  public static Object getBean(String name, Object... args) {
    return applicationContext().getBean(name, args);
  }

  public static <T> T getBean(Class<T> requiredType) {
    return applicationContext().getBean(requiredType);
  }

  public static <T> T getBean(Class<T> requiredType, Object... args) {
    return applicationContext().getBean(requiredType, args);
  }

  public static <T> Map<String, T> getBeansOfType(Class<T> type) {
    return applicationContext().getBeansOfType(type);
  }

  public static <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons,
      boolean allowEagerInit) {
    return applicationContext().getBeansOfType(type, includeNonSingletons, allowEagerInit);
  }

  public static AutowireCapableBeanFactory autowireCapableBeanFactory() {
    return applicationContext().getAutowireCapableBeanFactory();
  }

  public static <T> T autowire(Class<T> beanClass, int autowireMode, boolean dependencyCheck) {
    return beanClass
        .cast(autowireCapableBeanFactory().autowire(beanClass, autowireMode, dependencyCheck));
  }

  public static <T> T autowireBean(T existingBean) {
    autowireCapableBeanFactory().autowireBean(existingBean);
    return existingBean;
  }

  public static <T> T autowireBeanProperties(T existingBean, int autowireMode,
      boolean dependencyCheck) {
    autowireCapableBeanFactory().autowireBeanProperties(existingBean, autowireMode,
        dependencyCheck);
    return existingBean;
  }

  public static Optional<HttpServletRequest> getCurrentHttpRequest() {
    return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
        .filter(ServletRequestAttributes.class::isInstance)
        .map(ServletRequestAttributes.class::cast).map(ServletRequestAttributes::getRequest);
  }

}
