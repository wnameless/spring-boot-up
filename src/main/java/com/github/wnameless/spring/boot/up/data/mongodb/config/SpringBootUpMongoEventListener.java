package com.github.wnameless.spring.boot.up.data.mongodb.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
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
import org.springframework.util.ReflectionUtils;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.wnameless.spring.boot.up.data.mongodb.cascade.CascadeDeleteCallback;
import com.github.wnameless.spring.boot.up.data.mongodb.cascade.CascadeSaveUpdateCallback;
import com.github.wnameless.spring.boot.up.data.mongodb.cascade.ParentRefCallback;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.SourceAndDocument;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterConvertFromMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterDeleteFromMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterSaveToMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.BeforeConvertToMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.BeforeDeleteFromMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.BeforeSaveToMongo;

public class SpringBootUpMongoEventListener extends AbstractMongoEventListener<Object> {

  private static final String ID = "_id";

  @Autowired
  private MongoOperations mongoOperations;

  private final Cache<Object, Object> afterDeleteSourceCache =
      Caffeine.newBuilder().maximumSize(16).build();

  // event.getSource() -> Java Object
  @Override
  public void onBeforeConvert(BeforeConvertEvent<Object> event) {
    // Cascade
    Object source = event.getSource();
    CascadeSaveUpdateCallback callback = new CascadeSaveUpdateCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);

    // Intercept
    mongoEventExecutor(BeforeConvertToMongo.class, source,
        new SourceAndDocument(source, event.getDocument()));
  }

  // event.getSource() -> Java Object
  @Override
  public void onBeforeSave(BeforeSaveEvent<Object> event) {
    // Intercept
    Object source = event.getSource();
    mongoEventExecutor(BeforeSaveToMongo.class, source,
        new SourceAndDocument(source, event.getDocument()));
  }

  // event.getSource() -> Java Object
  @Override
  public void onAfterSave(AfterSaveEvent<Object> event) {
    // Cascade
    Object source = event.getSource();
    ParentRefCallback callback = new ParentRefCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);

    // Intercept
    mongoEventExecutor(AfterSaveToMongo.class, source,
        new SourceAndDocument(source, event.getDocument()));
  }

  // event.getSource() -> BSON Document
  @Override
  public void onAfterLoad(AfterLoadEvent<Object> event) {}

  // event.getSource() -> Java Object
  @Override
  public void onAfterConvert(AfterConvertEvent<Object> event) {
    Object source = event.getSource();
    mongoEventExecutor(AfterConvertFromMongo.class, source,
        new SourceAndDocument(source, event.getDocument()));
  }

  // event.getSource() -> BSON Document
  @Override
  public void onBeforeDelete(BeforeDeleteEvent<Object> event) {
    // Cascade
    Object docId = event.getSource().get(ID);
    Object source = mongoOperations.findById(docId, event.getType());
    CascadeDeleteCallback callback = new CascadeDeleteCallback(source, mongoOperations);
    ReflectionUtils.doWithFields(source.getClass(), callback);

    // Intercept
    mongoEventExecutor(BeforeDeleteFromMongo.class, source,
        new SourceAndDocument(source, event.getDocument()));
    afterDeleteSourceCache.put(docId, source);
  }

  // event.getSource() -> BSON Document
  @Override
  public void onAfterDelete(AfterDeleteEvent<Object> event) {
    // Intercept
    Object docId = event.getSource().get(ID);
    Object source = afterDeleteSourceCache.asMap().remove(docId);
    if (source != null) {
      mongoEventExecutor(AfterDeleteFromMongo.class, source,
          new SourceAndDocument(source, event.getDocument()));
    }
  }

  private void mongoEventExecutor(Class<? extends Annotation> annoType, Object target,
      SourceAndDocument sourceAndDocument) {
    Set<String> executedNames = new HashSet<>();
    for (Method method : target.getClass().getDeclaredMethods()) {
      if (method.isAnnotationPresent(annoType)) {
        executedNames.add(method.getName());
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
        } else {
          ReflectionUtils.invokeMethod(method, target);
        }
      }
    }
    for (Method method : target.getClass().getMethods()) {
      if (!executedNames.contains(method.getName()) && method.isAnnotationPresent(annoType)) {
        method.setAccessible(true);
        if (method.getParameterTypes().length == 1
            && method.getParameterTypes()[0].isAssignableFrom(SourceAndDocument.class)) {
          ReflectionUtils.invokeMethod(method, target, sourceAndDocument);
        } else {
          ReflectionUtils.invokeMethod(method, target);
        }
      }
    }
  }

}
