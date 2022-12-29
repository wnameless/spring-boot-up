package com.github.wnameless.spring.boot.up.data.mongodb;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
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

public class SpringBootUpMongoEventListener extends AbstractMongoEventListener<Object> {

  private static final int CASCADE_DELETE_CALLBACK_CACHE_SIZE = 256;
  private static final int BEFORE_DELETE_ACTION_CACHE_SIZE = 256;
  private static final int AFTER_DELETE_ACTION_CACHE_SIZE = 256;
  private static final int AFTER_DELETE_OBJECT_CACHE_SIZE = 64;

  private final Map<Object, CascadeDeleteCallback> cascadeDeleteCallbacks =
      Collections.synchronizedMap(

          new LinkedHashMap<Object, CascadeDeleteCallback>() {

            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<Object, CascadeDeleteCallback> entry) {
              return size() > CASCADE_DELETE_CALLBACK_CACHE_SIZE;
            }

          });

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
    // Cascade
    Object source = event.getSource();
    CascadeSaveUpdateCallback callback = new CascadeSaveUpdateCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);

    // Annotation event joint point
    Object target = event.getSource();

    Set<String> executedNames = new HashSet<>();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(BeforeConvertToMongo.class)) {
        executedNames.add(method.getName());
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
    for (Method method : target.getClass().getMethods()) {
      if (!executedNames.contains(method.getName())
          && method.isAnnotationPresent(BeforeConvertToMongo.class)) {
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
    // Annotation event joint point
    Object target = event.getSource();

    Set<String> executedNames = new HashSet<>();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(BeforeSaveToMongo.class)) {
        executedNames.add(method.getName());
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
    for (Method method : target.getClass().getMethods()) {
      if (!executedNames.contains(method.getName())
          && method.isAnnotationPresent(BeforeSaveToMongo.class)) {
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
    // Cascade
    Object source = event.getSource();
    ParentRefCallback callback = new ParentRefCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);

    // Annotation event point
    Object target = event.getSource();

    Set<String> executedNames = new HashSet<>();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(AfterSaveToMongo.class)) {
        executedNames.add(method.getName());
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
    for (Method method : target.getClass().getMethods()) {
      if (!executedNames.contains(method.getName())
          && method.isAnnotationPresent(AfterSaveToMongo.class)) {
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
    // Cascade
    Object source = event.getSource();
    CascadeDeleteCallback callback = new CascadeDeleteCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);

    // Cache deletable callback
    Object docId = event.getDocument().get("_id");
    if (docId != null && !callback.getDeletableIds().isEmpty()) {
      cascadeDeleteCallbacks.put(docId, callback);
    }

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
    for (Method method : target.getClass().getMethods()) {
      if (!executedNames.contains(method.getName())
          && method.isAnnotationPresent(AfterConvertFromMongo.class)) {
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
    // Annotation event joint point
    Object docId = event.getSource().get("_id");

    if (beforeDeleteActions.containsKey(docId)) {
      Class<?> type = beforeDeleteActions.get(docId);
      Query searchQuery = new Query(Criteria.where("_id").is(docId));
      Object target = mongoOperations.findOne(searchQuery, type);

      Set<String> executedNames = new HashSet<>();
      for (Method method : type.getDeclaredMethods()) {
        if (method.isAnnotationPresent(BeforeDeleteFromMongo.class)) {
          executedNames.add(method.getName());
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
      for (Method method : type.getMethods()) {
        if (!executedNames.contains(method.getName())
            && method.isAnnotationPresent(BeforeDeleteFromMongo.class)) {
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
      Class<?> type = afterDeleteActions.get(docId);
      Query searchQuery = new Query(Criteria.where("_id").is(docId));
      Object target = mongoOperations.findOne(searchQuery, type);
      afterDeleteObjects.put(docId, target);
    }
  }

  // event.getSource() -> BSON Document
  @Override
  public void onAfterDelete(AfterDeleteEvent<Object> event) {
    // Cascade
    Object docId = event.getSource().get("_id");
    if (cascadeDeleteCallbacks.containsKey(docId)) {
      CascadeDeleteCallback callback = cascadeDeleteCallbacks.remove(docId);
      for (DeletableId deletableId : callback.getDeletableIds()) {
        Query searchQuery = new Query(Criteria.where("_id").is(deletableId.getId()));
        mongoOperations.remove(searchQuery, deletableId.getType());
      }
    }

    // Annotation event joint point
    if (afterDeleteActions.containsKey(docId)) {
      Class<?> type = afterDeleteActions.get(docId);
      Object target = afterDeleteObjects.get(docId);

      Set<String> executedNames = new HashSet<>();
      for (Method method : type.getDeclaredMethods()) {
        if (method.isAnnotationPresent(AfterDeleteFromMongo.class)) {
          executedNames.add(method.getName());
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
      for (Method method : type.getMethods()) {
        if (!executedNames.contains(method.getName())
            && method.isAnnotationPresent(AfterDeleteFromMongo.class)) {
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
