package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.function.Function;
import com.github.wnameless.json.base.JsonObjectBase;
import com.github.wnameless.spring.boot.up.jsf.model.JsfData;
import com.github.wnameless.spring.boot.up.jsf.model.JsfSchema;

public interface JsfDocument<JD extends JsfData<JS, ID>, JS extends JsfSchema<ID>, ID>
    extends JsonSchemaForm, JsfVersioning {

  JD getJsfData();

  default String getFormType() {
    return getClass().getSimpleName();
  }

  default String getFormBranch() {
    return JsfConfig.getDefaultBranchName();
  }

  default Map<String, Object> getFormData() {
    return getJsfData().getFormData();
  }

  default void setFormData(Map<String, Object> formData) {
    getJsfData().setFormData(formData);
  }

  default void setFormData(JsonObjectBase<?> formData) {
    getJsfData().setFormData(formData.toMap());
  }

  default Map<String, Object> getSchema() {
    if (jsfSchemaStrategy() != null) {
      return jsfSchemaStrategy().apply(getJsfData().getJsfSchema()).getSchema();
    }

    return getJsfData().getJsfSchema().getSchema();
  }

  default void setSchema(Map<String, Object> schema) {
    getJsfData().getJsfSchema().setSchema(schema);
  }

  default Map<String, Object> getUiSchema() {
    if (jsfSchemaStrategy() != null) {
      return jsfSchemaStrategy().apply(getJsfData().getJsfSchema()).getUiSchema();
    }

    return getJsfData().getJsfSchema().getUiSchema();
  }

  default void setUiSchema(Map<String, Object> uiSchema) {
    getJsfData().getJsfSchema().setUiSchema(uiSchema);
  }

  default Function<JS, JS> jsfSchemaStrategy() {
    return null;
  }

}
