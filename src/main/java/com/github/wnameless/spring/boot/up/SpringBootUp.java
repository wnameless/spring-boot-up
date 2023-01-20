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

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
   * @see {@link ApplicationContext#getBean(Class)}
   */
  public static <T> T getBean(Class<T> requiredType) {
    return applicationContext().getBean(requiredType);
  }

  /**
   * @see {@link ApplicationContext#getBean(Class, Object...)}
   */
  public static <T> T getBean(Class<T> requiredType, Object... args) {
    return applicationContext().getBean(requiredType, args);
  }

  /**
   * @see {@link ApplicationContext#getBean(String)}
   */
  public static Object getBean(String name) {
    return applicationContext().getBean(name);
  }

  /**
   * @see {@link ApplicationContext#getBean(Class)}
   */
  public static <T> T getBean(String name, Class<T> requiredType) {
    return applicationContext().getBean(name, requiredType);
  }

  /**
   * @see {@link ApplicationContext#getBean(Class, Object...)}
   */
  public static Object getBean(String name, Object... args) {
    return applicationContext().getBean(name, args);
  }

  /**
   * @see {@link ApplicationContext#getBeansOfType(Class)}
   */
  public static <T> Map<String, T> getBeansOfType(Class<T> type) {
    return applicationContext().getBeansOfType(type);
  }

  /**
   * @see {@link ApplicationContext#getBeansOfType(Class, boolean, boolean)}
   */
  public static <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons,
      boolean allowEagerInit) {
    return applicationContext().getBeansOfType(type, includeNonSingletons, allowEagerInit);
  }

  /**
   * @see {@link ApplicationContext#getBeansWithAnnotation(Class)}
   */
  public static Map<String, Object> getBeansWithAnnotation(
      Class<? extends Annotation> annotationType) {
    return applicationContext().getBeansWithAnnotation(annotationType);
  }

  /**
   * Finds current {@link HttpServletRequest}.
   * 
   * @return a {@link Optional} of {@link HttpServletRequest}
   */
  public static Optional<HttpServletRequest> currentHttpServletRequest() {
    return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
        .filter(ServletRequestAttributes.class::isInstance)
        .map(ServletRequestAttributes.class::cast).map(ServletRequestAttributes::getRequest);
  }

  /**
   * Finds current {@link HttpServletResponse}.
   * 
   * @return a {@link Optional} of {@link HttpServletResponse}
   */
  public static Optional<HttpServletResponse> currentHttpServletResponse() {
    return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
        .filter(ServletRequestAttributes.class::isInstance)
        .map(ServletRequestAttributes.class::cast).map(ServletRequestAttributes::getResponse);
  }

  /**
   * @see {@link MessageSource#getMessage(String, Object[], java.util.Locale)}
   */
  public static String getMessage(String code, Object... args) {
    return getBean(MessageSource.class).getMessage(code, args, LocaleContextHolder.getLocale());
  }

  /**
   * @see {@link MessageSource#getMessage(String, Object[], String, java.util.Locale)}
   */
  public static String getMessage(String code, String defaultMessage, Object... args) {
    return getBean(MessageSource.class).getMessage(code, args, defaultMessage,
        LocaleContextHolder.getLocale());
  }

}
