package com.github.wnameless.spring.boot.up.jsf.model;

import java.time.LocalDateTime;
import java.util.Map;

public interface JsfSchema<ID> {

  ID getId();

  String getFormType();

  void setFormType(String formType);

  String getFormBranch();

  void setFormBranch(String formBranch);

  LocalDateTime getVersion();

  void setVersion(LocalDateTime version);

  Map<String, Object> getSchema();

  void setSchema(Map<String, Object> schema);

  Map<String, Object> getUiSchema();

  void setUiSchema(Map<String, Object> uiSchema);

}
