package com.github.wnameless.spring.boot.up.jsf.model;

import java.time.LocalDateTime;
import java.util.Map;
import com.github.wnameless.spring.boot.up.jsf.JsfVersioning;

public interface JsfSchema<ID> extends JsfVersioning {

  ID getId();

  void setFormType(String formType);

  void setFormBranch(String formBranch);

  LocalDateTime getVersion();

  void setVersion(LocalDateTime version);

  Map<String, Object> getSchema();

  void setSchema(Map<String, Object> schema);

  Map<String, Object> getUiSchema();

  void setUiSchema(Map<String, Object> uiSchema);

}
