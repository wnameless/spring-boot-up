package com.github.wnameless.spring.boot.up.data.mongodb.interceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterLoadEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.BeforeSaveEvent;
import org.springframework.util.ReflectionUtils;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterConvertFromMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterDeleteFromMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterSaveToMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.BeforeConvertToMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.BeforeDeleteFromMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.BeforeSaveToMongo;

public class InterceptorMongoEventListener extends AbstractMongoEventListener<Object> {

  // event.getSource() -> Java Object
  @Override
  public void onBeforeConvert(BeforeConvertEvent<Object> event) {
    Object target = event.getSource();
    mongoEventExecutor(BeforeConvertToMongo.class, target,
        new SourceAndDocument(target, event.getDocument()));
  }

  // event.getSource() -> Java Object
  @Override
  public void onBeforeSave(BeforeSaveEvent<Object> event) {
    Object target = event.getSource();
    mongoEventExecutor(BeforeSaveToMongo.class, target,
        new SourceAndDocument(target, event.getDocument()));
  }

  // event.getSource() -> Java Object
  @Override
  public void onAfterSave(AfterSaveEvent<Object> event) {
    Object target = event.getSource();
    mongoEventExecutor(AfterSaveToMongo.class, target,
        new SourceAndDocument(target, event.getDocument()));
  }

  // event.getSource() -> BSON Document
  @Override
  public void onAfterLoad(AfterLoadEvent<Object> event) {}

  // event.getSource() -> Java Object
  @Override
  public void onAfterConvert(AfterConvertEvent<Object> event) {
    Object target = event.getSource();
    mongoEventExecutor(AfterConvertFromMongo.class, target,
        new SourceAndDocument(target, event.getDocument()));
  }

  // event.getSource() -> BSON Document
  @Override
  public void onBeforeDelete(BeforeDeleteEvent<Object> event) {
    Object target;
    try {
      target = event.getType().getDeclaredConstructor().newInstance();
      mongoEventExecutor(BeforeDeleteFromMongo.class, target,
          new SourceAndDocument(event.getSource(), event.getDocument()));
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  // event.getSource() -> BSON Document
  @Override
  public void onAfterDelete(AfterDeleteEvent<Object> event) {
    Object target;
    try {
      target = event.getType().getDeclaredConstructor().newInstance();
      mongoEventExecutor(AfterDeleteFromMongo.class, target,
          new SourceAndDocument(event.getSource(), event.getDocument()));
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
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


