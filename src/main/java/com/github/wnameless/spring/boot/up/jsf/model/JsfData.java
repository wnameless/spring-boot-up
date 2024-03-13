package com.github.wnameless.spring.boot.up.jsf.model;

import java.time.LocalDateTime;
import java.util.Map;
import com.github.wnameless.spring.boot.up.jsf.JsfVersioning;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaForm;
import com.github.wnameless.spring.boot.up.web.IdProvider;

public interface JsfData<JS extends JsfSchema<ID>, ID>
    extends JsonSchemaForm, JsfVersioning, IdProvider<ID> {

  JS getJsfSchema();

  void setJsfSchema(JS JsfSchema);

  LocalDateTime getVersion();

  void setVersion(LocalDateTime version);

  Map<String, Object> getFormData();

  void setFormData(Map<String, Object> formData);

  @Override
  default String getFormType() {
    return getJsfSchema().getFormType();
  }

  @Override
  default String getFormBranch() {
    return getJsfSchema().getFormBranch();
  }

  @Override
  default Map<String, Object> getSchema() {
    return getJsfSchema().getSchema();
  }

  @Override
  default Map<String, Object> getUiSchema() {
    return getJsfSchema().getUiSchema();
  }

}
