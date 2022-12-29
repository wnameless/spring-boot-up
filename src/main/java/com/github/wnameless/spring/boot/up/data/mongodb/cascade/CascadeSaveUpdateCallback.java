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
package com.github.wnameless.spring.boot.up.data.mongodb.cascade;

import static com.github.wnameless.spring.boot.up.data.mongodb.cascade.CascadeType.ALL;
import static com.github.wnameless.spring.boot.up.data.mongodb.cascade.CascadeType.SAVE;
import static com.github.wnameless.spring.boot.up.data.mongodb.cascade.CascadeType.UPDATE;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.ReflectionUtils;
import com.github.wnameless.spring.boot.up.data.mongodb.cascade.annotation.CascadeRef;

public class CascadeSaveUpdateCallback implements ReflectionUtils.FieldCallback {

  private final Object source;
  private final MongoOperations mongoOperations;

  public CascadeSaveUpdateCallback(Object source, MongoOperations mongoOperations) {
    this.source = source;
    this.mongoOperations = mongoOperations;
  }

  @Override
  public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
    ReflectionUtils.makeAccessible(field);

    if (!field.isAnnotationPresent(DBRef.class) || !field.isAnnotationPresent(CascadeRef.class)) {
      return;
    }

    CascadeRef cascade = AnnotationUtils.getAnnotation(field, CascadeRef.class);
    List<CascadeType> cascadeTypes = Arrays.asList(cascade.value());
    if (!cascadeTypes.contains(ALL) && !cascadeTypes.contains(SAVE)
        && !cascadeTypes.contains(UPDATE)) {
      return;
    }

    Object fieldValue = field.get(source);
    if (fieldValue == null) return;
    // Collection field
    if (Collection.class.isAssignableFrom(fieldValue.getClass())) {
      Collection<?> collection;
      if (Map.class.isAssignableFrom(fieldValue.getClass())) {
        collection = ((Map<?, ?>) fieldValue).values();
      } else {
        collection = (Collection<?>) fieldValue;
      }
      for (Object element : collection) {
        cascadeUpsert(element, cascadeTypes);
      }
    } else { // Non-Collection field
      cascadeUpsert(fieldValue, cascadeTypes);
    }
  }

  private void cascadeUpsert(Object value, List<CascadeType> cascadeTypes)
      throws IllegalArgumentException, IllegalAccessException {
    IdFieldCallback callback = new IdFieldCallback();
    ReflectionUtils.doWithFields(value.getClass(), callback);

    if (callback.isIdFound()) {
      Object id = callback.getId(value);
      if (id == null) {
        if (cascadeTypes.contains(ALL) || cascadeTypes.contains(SAVE)) {
          mongoOperations.save(value);
        }
      } else { // id != null
        if (cascadeTypes.contains(ALL) || cascadeTypes.contains(UPDATE)) {
          mongoOperations.save(value);
        }
      }
    }
  }

}
