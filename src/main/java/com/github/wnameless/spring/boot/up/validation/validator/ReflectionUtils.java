package com.github.wnameless.spring.boot.up.validation.validator;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

final class ReflectionUtils {

  private ReflectionUtils() {}

  public static List<Method> extractStaticMethods(Class<?> clazz) {
    Method[] allMethods = clazz.getMethods();
    List<Method> staticMethods = new ArrayList<>(allMethods.length);

    for (Method method : allMethods) {
      if (Modifier.isStatic(method.getModifiers())) {
        staticMethods.add(method);
      }
    }
    return staticMethods;
  }

}
