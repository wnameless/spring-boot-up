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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.ReflectionUtils;

public class AnnotationMongoEventListener extends AbstractMongoEventListener<Object> {

  private static final int CACHE_SIZE = 256;

  private final Map<Object, Class<?>> beforeDeleteActions = Collections.synchronizedMap(

      new LinkedHashMap<Object, Class<?>>(CACHE_SIZE) {

        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, Class<?>> entry) {
          return size() > CACHE_SIZE;
        }

      });

  private final Map<Object, Class<?>> afterDeleteActions = Collections.synchronizedMap(

      new LinkedHashMap<Object, Class<?>>(CACHE_SIZE) {

        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, Class<?>> entry) {
          return size() > CACHE_SIZE;
        }

      });

  private final Map<Object, Object> afterDeleteObjects = Collections.synchronizedMap(

      new LinkedHashMap<Object, Object>(CACHE_SIZE) {

        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, Object> entry) {
          return size() > 8;
        }

      });

  @Autowired
  private MongoOperations mongoOperations;


  // event.getSource() -> Java Object
  @Override
  public void onBeforeConvert(BeforeConvertEvent<Object> event) {
    Object target = event.getSource();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(BeforeConvertToMongo.class)) {
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(event);
          ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
        } else {
          ReflectionUtils.invokeMethod(method, target);
        }
      }
    }
  }

  // event.getSource() -> Java Object
  @Override
  public void onBeforeSave(BeforeSaveEvent<Object> event) {
    Object target = event.getSource();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(BeforeSaveToMongo.class)) {
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(event);
          ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
        } else {
          ReflectionUtils.invokeMethod(method, target);
        }
      }
    }
  }

  // event.getSource() -> Java Object
  @Override
  public void onAfterSave(AfterSaveEvent<Object> event) {
    Object target = event.getSource();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(AfterSaveToMongo.class)) {
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(event);
          ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
        } else {
          ReflectionUtils.invokeMethod(method, target);
        }
      }
    }
  }

  // event.getSource() -> BSON Document
  @Override
  public void onAfterLoad(AfterLoadEvent<Object> event) {}

  // event.getSource() -> Java Object
  @Override
  public void onAfterConvert(AfterConvertEvent<Object> event) {
    boolean beforeDeleteMethod = false;
    boolean afterDeleteMethod = false;

    Object target = event.getSource();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(AfterConvertFromMongo.class)) {
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(event);
          ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
        } else {
          ReflectionUtils.invokeMethod(method, target);
        }
      }

      if (method.isAnnotationPresent(BeforeDeleteFromMongo.class)) {
        beforeDeleteMethod = true;
      }
      if (method.isAnnotationPresent(AfterDeleteFromMongo.class)) {
        afterDeleteMethod = true;
      }
    }

    // Cache beforeDelete/afterDelete id and class
    Object docId = event.getDocument().get("_id");
    if (docId != null && beforeDeleteMethod) {
      beforeDeleteActions.put(docId, target.getClass());
    }
    if (docId != null && afterDeleteMethod) {
      afterDeleteActions.put(docId, target.getClass());
    }
  }

  // event.getSource() -> BSON Document
  @Override
  public void onBeforeDelete(BeforeDeleteEvent<Object> event) {
    Object docId = event.getSource().get("_id");

    if (beforeDeleteActions.containsKey(docId)) {
      Class<?> type = beforeDeleteActions.get(docId);
      Query searchQuery = new Query(Criteria.where("_id").is(docId));
      Object target = mongoOperations.findOne(searchQuery, type);

      for (Method method : type.getDeclaredMethods()) {
        if (method.isAnnotationPresent(BeforeDeleteFromMongo.class)) {
          method.setAccessible(true);
          if (method.getParameterTypes().length == 1
              && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
            SourceAndDocument sourceAndDocument = new SourceAndDocument(event);
            ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
          } else {
            ReflectionUtils.invokeMethod(method, target);
          }
        }
      }
    }

    if (afterDeleteActions.containsKey(docId)) {
      Class<?> type = beforeDeleteActions.get(docId);
      Query searchQuery = new Query(Criteria.where("_id").is(docId));
      Object target = mongoOperations.findOne(searchQuery, type);
      afterDeleteObjects.put(docId, target);
    }
  }

  // event.getSource() -> BSON Document
  @Override
  public void onAfterDelete(AfterDeleteEvent<Object> event) {
    Object docId = event.getSource().get("_id");

    if (afterDeleteActions.containsKey(docId)) {
      Class<?> type = afterDeleteActions.get(docId);
      Object target = afterDeleteObjects.get(docId);

      for (Method method : type.getDeclaredMethods()) {
        if (method.isAnnotationPresent(AfterDeleteFromMongo.class)) {
          method.setAccessible(true);
          if (method.getParameterTypes().length == 1
              && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
            SourceAndDocument sourceAndDocument = new SourceAndDocument(event);
            ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
          } else {
            ReflectionUtils.invokeMethod(method, target);
          }
        }
      }
    }
  }

}
