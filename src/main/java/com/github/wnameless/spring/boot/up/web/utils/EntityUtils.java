package com.github.wnameless.spring.boot.up.web.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.data.annotation.Id;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class EntityUtils {

  public <T> Optional<T> tryDuplicateIdOnlyEntity(T src) {
    Optional<T> mock = Optional.empty();
    try {
      var idOnly = duplicateIdOnlyEntity(src);
      return Optional.of(idOnly);
    } catch (Exception e) {
      log.error("Duplicate Id-only entity failed", e);
      return mock;
    }
  }

  public <T> T duplicateIdOnlyEntity(T src)
      throws InstantiationException, IllegalAccessException, IllegalArgumentException,
      InvocationTargetException, NoSuchMethodException, SecurityException {
    // Mock an empty item for user without permission
    @SuppressWarnings("unchecked")
    var mock = (T) src.getClass().getDeclaredConstructor().newInstance();
    // Retain Id value
    var idFieldName =
        findAnnotatedFieldName(src.getClass(), Id.class, jakarta.persistence.Id.class);
    if (idFieldName.isPresent()) copyField(src, mock, idFieldName.get());

    log.warn("User can NOT read {} resource, a mock ID-only entity is created.",
        src.getClass().getSimpleName());
    return mock;
  }

  @SafeVarargs
  public Optional<String> findAnnotatedFieldName(Class<?> entityClass,
      Class<? extends Annotation>... annoTypes) {
    List<Field> fields = FieldUtils.getAllFieldsList(entityClass);
    for (Field field : fields) {
      if (Arrays.asList(annoTypes).stream().anyMatch(field::isAnnotationPresent)) {
        return Optional.of(field.getName());
      }
    }
    return Optional.empty();
  }

  public void copyField(Object source, Object target, String fieldName) {
    try {
      Field sourceField = FieldUtils.getAllFieldsList(source.getClass()).stream()
          .filter(f -> f.getName().equals(fieldName)).findFirst().get();
      Field targetField = FieldUtils.getAllFieldsList(target.getClass()).stream()
          .filter(f -> f.getName().equals(fieldName)).findFirst().get();
      sourceField.setAccessible(true);
      targetField.setAccessible(true);
      Object value = sourceField.get(source);
      targetField.set(target, value);
    } catch (IllegalAccessException e) {
      log.error("Copy field[" + fieldName + "] value failed", e);
    }
  }

}
