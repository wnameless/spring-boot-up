package com.github.wnameless.spring.boot.up.data.mongodb.cascade;

import java.lang.reflect.Field;
import org.springframework.data.annotation.Id;
import org.springframework.util.ReflectionUtils;

public class IdFieldCallback implements ReflectionUtils.FieldCallback {

  private Field idField;
  private boolean idFound;
  private String idFieldName;

  @Override
  public void doWith(final Field field) throws IllegalArgumentException, IllegalAccessException {
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

  public Object getId(Object obj) throws IllegalArgumentException, IllegalAccessException {
    return idField.get(obj);
  }

}
