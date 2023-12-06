package com.github.wnameless.spring.boot.up.tagging;

public interface TagTemplate {

  String getGroupTitle();

  String getTagName();

  String getEntityType();

  void setEntityType(String entityType);

  String getUsername();

  default void setEntityTypeByClass(Class<?> type) {
    setEntityType(type.getName());
  }

  default Class<?> getEntityTypeByClass() throws ClassNotFoundException {
    return Class.forName(getEntityType());
  }

}
