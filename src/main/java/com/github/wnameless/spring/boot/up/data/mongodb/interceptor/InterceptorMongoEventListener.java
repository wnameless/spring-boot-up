package com.github.wnameless.spring.boot.up.data.mongodb.interceptor;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterConvertFromMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterDeleteFromMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterSaveToMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.BeforeConvertToMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.BeforeDeleteFromMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.BeforeSaveToMongo;


public class InterceptorMongoEventListener extends AbstractMongoEventListener<Object> {

  private static final String ID = "_id";

  @Value("${spring.boot.up.data.mongodb.interceptor.before_delete_action.cache.size:256}")
  private int BEFORE_DELETE_ACTION_CACHE_SIZE = 256;
  @Value("${spring.boot.up.data.mongodb.interceptor.after_delete_action.cache.size:256}")
  private int AFTER_DELETE_ACTION_CACHE_SIZE = 256;
  @Value("${spring.boot.up.data.mongodb.interceptor.after_delete_object.cache.size:16}")
  private int AFTER_DELETE_OBJECT_CACHE_SIZE = 16;

  private final Map<Object, Class<?>> beforeDeleteActions = Collections.synchronizedMap(

      new LinkedHashMap<Object, Class<?>>() {

        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, Class<?>> entry) {
          return size() > BEFORE_DELETE_ACTION_CACHE_SIZE;
        }

      });

  private final Map<Object, Class<?>> afterDeleteActions = Collections.synchronizedMap(

      new LinkedHashMap<Object, Class<?>>() {

        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, Class<?>> entry) {
          return size() > AFTER_DELETE_ACTION_CACHE_SIZE;
        }

      });

  private final Map<Object, Object> afterDeleteObjects = Collections.synchronizedMap(

      new LinkedHashMap<Object, Object>() {

        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<Object, Object> entry) {
          return size() > AFTER_DELETE_OBJECT_CACHE_SIZE;
        }

      });

  @Autowired
  private MongoOperations mongoOperations;


  // event.getSource() -> Java Object
  @Override
  public void onBeforeConvert(BeforeConvertEvent<Object> event) {
    // Annotation event joint point
    Object target = event.getSource();

    Set<String> executedNames = new HashSet<>();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(BeforeConvertToMongo.class)) {
        executedNames.add(method.getName());
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(target, event.getDocument());
          ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
        } else {
          ReflectionUtils.invokeMethod(method, target);
        }
      }
    }
    for (Method method : target.getClass().getMethods()) {
      if (!executedNames.contains(method.getName())
          && method.isAnnotationPresent(BeforeConvertToMongo.class)) {
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(target, event.getDocument());
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
    // Annotation event joint point
    Object target = event.getSource();

    Set<String> executedNames = new HashSet<>();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(BeforeSaveToMongo.class)) {
        executedNames.add(method.getName());
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(target, event.getDocument());
          ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
        } else {
          ReflectionUtils.invokeMethod(method, target);
        }
      }
    }
    for (Method method : target.getClass().getMethods()) {
      if (!executedNames.contains(method.getName())
          && method.isAnnotationPresent(BeforeSaveToMongo.class)) {
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(target, event.getDocument());
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
    // Annotation event point
    Object target = event.getSource();

    Set<String> executedNames = new HashSet<>();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(AfterSaveToMongo.class)) {
        executedNames.add(method.getName());
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(target, event.getDocument());
          ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
        } else {
          ReflectionUtils.invokeMethod(method, target);
        }
      }
    }
    for (Method method : target.getClass().getMethods()) {
      if (!executedNames.contains(method.getName())
          && method.isAnnotationPresent(AfterSaveToMongo.class)) {
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(target, event.getDocument());
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
    Object docId = event.getDocument().get(ID);
    // Annotation event joint point
    boolean beforeDeleteMethod = false;
    boolean afterDeleteMethod = false;

    Object target = event.getSource();

    Set<String> executedNames = new HashSet<>();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(AfterConvertFromMongo.class)) {
        executedNames.add(method.getName());
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(target, event.getDocument());
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
    for (Method method : target.getClass().getMethods()) {
      if (!executedNames.contains(method.getName())
          && method.isAnnotationPresent(AfterConvertFromMongo.class)) {
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          SourceAndDocument sourceAndDocument = new SourceAndDocument(target, event.getDocument());
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
    Object docId = event.getSource().get(ID);
    // Annotation event joint point
    if (beforeDeleteActions.containsKey(docId)) {
      Class<?> type = beforeDeleteActions.remove(docId);
      Query searchQuery = new Query(Criteria.where(ID).is(docId));
      Object target = mongoOperations.findOne(searchQuery, type);

      Set<String> executedNames = new HashSet<>();
      for (Method method : type.getDeclaredMethods()) {
        if (method.isAnnotationPresent(BeforeDeleteFromMongo.class)) {
          executedNames.add(method.getName());
          method.setAccessible(true);
          if (method.getParameterTypes().length == 1
              && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
            SourceAndDocument sourceAndDocument =
                new SourceAndDocument(target, event.getDocument());
            ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
          } else {
            ReflectionUtils.invokeMethod(method, target);
          }
        }
      }
      for (Method method : type.getMethods()) {
        if (!executedNames.contains(method.getName())
            && method.isAnnotationPresent(BeforeDeleteFromMongo.class)) {
          method.setAccessible(true);
          if (method.getParameterTypes().length == 1
              && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
            SourceAndDocument sourceAndDocument =
                new SourceAndDocument(target, event.getDocument());
            ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
          } else {
            ReflectionUtils.invokeMethod(method, target);
          }
        }
      }
    }

    if (afterDeleteActions.containsKey(docId)) {
      Class<?> type = afterDeleteActions.get(docId);
      Query searchQuery = new Query(Criteria.where(ID).is(docId));
      Object target = mongoOperations.findOne(searchQuery, type);
      afterDeleteObjects.put(docId, target);
    }
  }

  // event.getSource() -> BSON Document
  @Override
  public void onAfterDelete(AfterDeleteEvent<Object> event) {
    Object docId = event.getSource().get(ID);
    // Annotation event joint point
    if (afterDeleteActions.containsKey(docId)) {
      Class<?> type = afterDeleteActions.remove(docId);
      Object target = afterDeleteObjects.remove(docId);

      Set<String> executedNames = new HashSet<>();
      for (Method method : type.getDeclaredMethods()) {
        if (method.isAnnotationPresent(AfterDeleteFromMongo.class)) {
          executedNames.add(method.getName());
          method.setAccessible(true);
          if (method.getParameterTypes().length == 1
              && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
            SourceAndDocument sourceAndDocument =
                new SourceAndDocument(target, event.getDocument());
            ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
          } else {
            ReflectionUtils.invokeMethod(method, target);
          }
        }
      }
      for (Method method : type.getMethods()) {
        if (!executedNames.contains(method.getName())
            && method.isAnnotationPresent(AfterDeleteFromMongo.class)) {
          method.setAccessible(true);
          if (method.getParameterTypes().length == 1
              && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
            SourceAndDocument sourceAndDocument =
                new SourceAndDocument(target, event.getDocument());
            ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
          } else {
            ReflectionUtils.invokeMethod(method, target);
          }
        }
      }
    }
  }

}
