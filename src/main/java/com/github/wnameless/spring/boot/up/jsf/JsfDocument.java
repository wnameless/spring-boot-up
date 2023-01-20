package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.Optional;
import com.github.wnameless.json.base.JsonObjectBase;
import com.github.wnameless.spring.boot.up.SpringBootUp;
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
    var documentStrategy = getJsfDocumentStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().formDataStrategy() != null) {
      return documentStrategy.get().formDataStrategy().apply(getJsfData().getFormData());
    }

    return getJsfData().getFormData();
  }

  default void setFormData(Map<String, Object> formData) {
    getJsfData().setFormData(formData);
  }

  default void setFormData(JsonObjectBase<?> formData) {
    getJsfData().setFormData(formData.toMap());
  }

  default Map<String, Object> getSchema() {
    var documentStrategy = getJsfDocumentStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().schemaStrategy() != null) {
      return documentStrategy.get().schemaStrategy().apply(getJsfData().getJsfSchema().getSchema());
    }

    return getJsfData().getJsfSchema().getSchema();
  }

  default void setSchema(Map<String, Object> schema) {
    getJsfData().getJsfSchema().setSchema(schema);
  }

  default Map<String, Object> getUiSchema() {
    var documentStrategy = getJsfDocumentStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().uiSchemaStrategy() != null) {
      return documentStrategy.get().uiSchemaStrategy()
          .apply(getJsfData().getJsfSchema().getUiSchema());
    }

    return getJsfData().getJsfSchema().getUiSchema();
  }

  default void setUiSchema(Map<String, Object> uiSchema) {
    getJsfData().getJsfSchema().setUiSchema(uiSchema);
  }

  default Optional<JsfDocumentStrategy> getJsfDocumentStrategy() {
    return SpringBootUp.getBeansOfType(JsfDocumentStrategy.class).values().stream()
        .filter(dc -> dc.getDocumentType().equals(this.getClass()))
        .filter(dc -> dc.activeStatus().getAsBoolean()).findFirst();
  }

}
