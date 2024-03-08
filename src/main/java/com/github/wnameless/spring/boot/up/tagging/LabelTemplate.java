package com.github.wnameless.spring.boot.up.tagging;

public interface LabelTemplate<ID> {

  ID getId();

  String getGroupTitle();

  void setGroupTitle(String groupTitle);

  String getLabelName();

  void setLabelName(String labelName);

  String getLabelColor();

  void setLabelColor(String code);

  String getEntityType();

  void setEntityType(String entityType);

  default void setEntityTypeByClass(Class<?> type) {
    setEntityType(type.getName());
  }

  default Class<?> getEntityTypeByClass() throws ClassNotFoundException {
    return Class.forName(getEntityType());
  }

  String getUsername();

  boolean isUserEditable();

  void setUserEditable(boolean userEditable);

}
