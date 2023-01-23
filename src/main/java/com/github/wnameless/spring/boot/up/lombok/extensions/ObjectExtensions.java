package com.github.wnameless.spring.boot.up.lombok.extensions;

import java.util.Objects;
import net.sf.rubycollect4j.Ruby;

public final class ObjectExtensions {

  private ObjectExtensions() {}

  public static boolean eq(Object obj, Object otherObj) {
    return Objects.equals(obj, otherObj);
  }

  public static boolean notEq(Object obj, Object otherObj) {
    return !Objects.equals(obj, otherObj);
  }

  public static boolean isBlank(Object obj) {
    return Ruby.Object.isBlank(obj);
  }

  public static boolean isPresent(Object obj) {
    return Ruby.Object.isPresent(obj);
  }

  public static <T> T orElse(T obj, T ifNull) {
    return obj != null ? obj : ifNull;
  }

}
