package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.Optional;
import com.github.wnameless.json.base.JsonObjectBase;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.model.JsfData;
import com.github.wnameless.spring.boot.up.jsf.model.JsfSchema;
import com.github.wnameless.spring.boot.up.jsf.service.JsfService;

public interface JsfDocument<JD extends JsfData<JS, ID>, JS extends JsfSchema<ID>, ID>
    extends JsonSchemaForm, JsfVersioning {

  JD getJsfData();

  void setJsfData(JD jsfData);

  default JD initJsfData() {
    return (JD) initJsfData(getFormType(), getFormBranch());
  }

  @SuppressWarnings({"unchecked"})
  default JD initJsfData(String formType, String formBranch) {
    return (JD) SpringBootUp.getBean(JsfService.class).newJsfData(formType, formBranch);
  }

  default JD getJsfDataInternal() {
    JD data = getJsfData();
    if (data == null) {
      data = initJsfData();
      setJsfData(data);
    }
    return data;
  }

  default String getFormType() {
    return getClass().getSimpleName();
  }

  default String getFormBranch() {
    return JsfConfig.getDefaultBranchName();
  }

  default Map<String, Object> getFormData() {
    var documentStrategy = getJsonSchemaFormStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().formDataStrategy() != null) {
      return documentStrategy.get().formDataStrategy().apply(getJsfDataInternal().getFormData());
    }

    return getJsfDataInternal().getFormData();
  }

  default void setFormData(Map<String, Object> formData) {
    getJsfDataInternal().setFormData(formData);
  }

  default void setFormData(JsonObjectBase<?> formData) {
    getJsfDataInternal().setFormData(formData.toMap());
  }

  default Map<String, Object> getSchema() {
    var documentStrategy = getJsonSchemaFormStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().schemaStrategy() != null) {
      return documentStrategy.get().schemaStrategy()
          .apply(getJsfDataInternal().getJsfSchema().getSchema());
    }

    return getJsfDataInternal().getJsfSchema().getSchema();
  }

  default void setSchema(Map<String, Object> schema) {
    getJsfDataInternal().getJsfSchema().setSchema(schema);
  }

  default Map<String, Object> getUiSchema() {
    var documentStrategy = getJsonSchemaFormStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().uiSchemaStrategy() != null) {
      return documentStrategy.get().uiSchemaStrategy()
          .apply(getJsfDataInternal().getJsfSchema().getUiSchema());
    }

    return getJsfDataInternal().getJsfSchema().getUiSchema();
  }

  default void setUiSchema(Map<String, Object> uiSchema) {
    getJsfDataInternal().getJsfSchema().setUiSchema(uiSchema);
  }

  default Optional<JsonSchemaFormStrategy> getJsonSchemaFormStrategy() {
    return SpringBootUp.getBeansOfType(JsonSchemaFormStrategy.class).values().stream()
        .filter(dc -> dc.getDocumentType().equals(this.getClass()))
        .filter(dc -> dc.activeStatus().getAsBoolean()).findFirst();
  }

}
