package com.github.wnameless.spring.boot.up;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.ResolvableType;
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
   * @see {@link ApplicationContext#getBeanNamesForType(ResolvableType)}
   * 
   * @return first matched bean
   */
  @SuppressWarnings("unchecked")
  public static <T> Optional<T> findGenericBean(Class<T> clazz, Class<?>... generics) {
    String[] beanNamesForType = applicationContext()
        .getBeanNamesForType(ResolvableType.forClassWithGenerics(clazz, generics));
    if (beanNamesForType.length == 0) return Optional.empty();
    return Optional.of((T) applicationContext().getBean(beanNamesForType[0]));
  }

  /**
   * @see {@link ApplicationContext#getBeanNamesForType(ResolvableType)}
   * 
   * @return all matched beans
   */
  @SuppressWarnings("unchecked")
  public static <T> List<T> findAllGenericBeans(Class<T> clazz, Class<?>... generics) {
    String[] beanNamesForType = applicationContext()
        .getBeanNamesForType(ResolvableType.forClassWithGenerics(clazz, generics));
    if (beanNamesForType.length == 0) return Collections.emptyList();
    return (List<T>) Arrays.asList(beanNamesForType).stream()
        .map(beanName -> applicationContext().getBean(beanName)).toList();
  }

  /**
   * @see {@link ApplicationContext#getBean(Class)}
   */
  public static <T> T getBean(Class<T> requiredType) {
    return applicationContext().getBean(requiredType);
  }

  /**
   * @see {@link ApplicationContext#getBean(Class)}
   */
  public static <T> Optional<T> findBean(Class<T> requiredType) {
    T bean;
    try {
      bean = applicationContext().getBean(requiredType);
      return Optional.of(bean);
    } catch (Exception e) {}
    return Optional.empty();
  }

  /**
   * @see {@link ApplicationContext#getBean(Class, Object...)}
   */
  public static <T> T getBean(Class<T> requiredType, Object... args) {
    return applicationContext().getBean(requiredType, args);
  }

  /**
   * @see {@link ApplicationContext#getBean(Class, Object...)}
   */
  public static <T> Optional<T> findBean(Class<T> requiredType, Object... args) {
    T bean;
    try {
      bean = applicationContext().getBean(requiredType, args);
      return Optional.of(bean);
    } catch (Exception e) {}
    return Optional.empty();
  }

  /**
   * @see {@link ApplicationContext#getBean(String)}
   */
  public static Object getBean(String name) {
    return applicationContext().getBean(name);
  }

  /**
   * @see {@link ApplicationContext#getBean(String)}
   */
  public static Optional<Object> findBean(String name) {
    Object bean;
    try {
      bean = applicationContext().getBean(name);
      return Optional.of(bean);
    } catch (Exception e) {}
    return Optional.empty();
  }

  /**
   * @see {@link ApplicationContext#getBean(String, Class)}
   */
  public static <T> T getBean(String name, Class<T> requiredType) {
    return applicationContext().getBean(name, requiredType);
  }

  /**
   * @see {@link ApplicationContext#getBean(String, Class)}
   */
  public static <T> Optional<T> findBean(String name, Class<T> requiredType) {
    T bean;
    try {
      bean = applicationContext().getBean(name, requiredType);
      return Optional.of(bean);
    } catch (Exception e) {}
    return Optional.empty();
  }

  /**
   * @see {@link ApplicationContext#getBean(String, Object...)}
   */
  public static Object getBean(String name, Object... args) {
    return applicationContext().getBean(name, args);
  }

  /**
   * @see {@link ApplicationContext#getBean(String, Object...)}
   */
  public static Optional<Object> findBean(String name, Object... args) {
    Object bean;
    try {
      bean = applicationContext().getBean(name, args);
      return Optional.of(bean);
    } catch (Exception e) {}
    return Optional.empty();
  }

  /**
   * @see {@link ApplicationContext#containsBean(String)}
   */
  public static boolean containsBean(String name) {
    return applicationContext().containsBean(name);
  }

  /**
   * @see {@link ApplicationContext#getBeanNamesForType(Class)}
   */
  public static <T> boolean containsBean(Class<T> type) {
    return applicationContext().getBeanNamesForType(type).length > 0;
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
  public static Optional<HttpServletRequest> findCurrentHttpServletRequest() {
    return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
        .filter(ServletRequestAttributes.class::isInstance)
        .map(ServletRequestAttributes.class::cast).map(ServletRequestAttributes::getRequest);
  }

  /**
   * Finds current {@link HttpServletResponse}.
   * 
   * @return a {@link Optional} of {@link HttpServletResponse}
   */
  public static Optional<HttpServletResponse> findCurrentHttpServletResponse() {
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
