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

import java.lang.reflect.Method;

import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.util.ReflectionUtils;

public class BeforeAndAfterConvertMongoEventListener
    extends AbstractMongoEventListener<Object> {

  @Override
  public void onBeforeConvert(BeforeConvertEvent<Object> event) {
    Object target = event.getSource();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(BeforeConvertToBSON.class)) {
        method.setAccessible(true);
        ReflectionUtils.invokeMethod(method, target);
      }
    }
  }

  @Override
  public void onAfterConvert(AfterConvertEvent<Object> event) {
    Object target = event.getSource();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(AfterConvertFromBSON.class)) {
        method.setAccessible(true);
        ReflectionUtils.invokeMethod(method, target);
      }
    }
  }

}
