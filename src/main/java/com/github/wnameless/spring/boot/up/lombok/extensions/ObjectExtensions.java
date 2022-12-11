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
package com.github.wnameless.spring.boot.up.lombok.extensions;

import java.util.Objects;

import net.sf.rubycollect4j.Ruby;

public final class ObjectExtensions {

  private ObjectExtensions() {
  }

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
