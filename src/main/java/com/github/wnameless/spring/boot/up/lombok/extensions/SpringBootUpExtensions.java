package com.github.wnameless.spring.boot.up.lombok.extensions;

import java.util.Map;
import com.github.wnameless.spring.boot.up.SpringBootUp;

public final class SpringBootUpExtensions {

  private SpringBootUpExtensions() {}

  public static <T> T toBean(Class<T> requiredType) {
    return SpringBootUp.getBean(requiredType);
  }

  public static <T> T toBean(Class<T> requiredType, Object... args) {
    return (T) SpringBootUp.getBean(requiredType, args);
  }

  public static <T> Map<String, T> toBeans(Class<T> type) {
    return SpringBootUp.getBeansOfType(type);
  }

  public static <T> Map<String, T> toBeans(Class<T> type, boolean includeNonSingletons,
      boolean allowEagerInit) {
    return SpringBootUp.getBeansOfType(type, includeNonSingletons, allowEagerInit);
  }

}
