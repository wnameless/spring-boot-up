/*
 *
 * Copyright 2020 Wei-Ming Wu
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
package com.github.wnameless.spring.boot.up.data.mongodb;

import java.lang.reflect.Field;

import org.springframework.data.annotation.Id;
import org.springframework.util.ReflectionUtils;

public class IdFieldCallback implements ReflectionUtils.FieldCallback {

  private Field idField;
  private boolean idFound;
  private String idFieldName;

  @Override
  public void doWith(final Field field)
      throws IllegalArgumentException, IllegalAccessException {
    ReflectionUtils.makeAccessible(field);

    if (field.isAnnotationPresent(Id.class)) {
      idField = field;
      idFound = true;
      idFieldName = field.getName();
    }
  }

  public boolean isIdFound() {
    return idFound;
  }

  public String getIdFieldName() {
    return idFieldName;
  }

  public Object getId(Object obj)
      throws IllegalArgumentException, IllegalAccessException {
    return idField.get(obj);
  }

}