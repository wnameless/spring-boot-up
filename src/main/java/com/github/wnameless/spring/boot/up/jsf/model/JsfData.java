package com.github.wnameless.spring.boot.up.jsf.model;

import java.time.LocalDateTime;
import java.util.Map;

public interface JsfData<JS extends JsfSchema<ID>, ID> {

  ID getId();

  JS getJsfSchema();

  void setJsfSchema(JS JsfSchema);

  LocalDateTime getVersion();

  void setVersion(LocalDateTime version);

  Map<String, Object> getFormData();

  void setFormData(Map<String, Object> formData);

}
