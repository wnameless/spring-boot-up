package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.Optional;
import com.github.wnameless.json.base.JsonObjectBase;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.model.JsfData;
import com.github.wnameless.spring.boot.up.jsf.model.JsfSchema;
import com.github.wnameless.spring.boot.up.jsf.service.JsfPatchService;
import com.github.wnameless.spring.boot.up.jsf.service.JsfService;

public interface JsfDocument<JD extends JsfData<JS, ID>, JS extends JsfSchema<ID>, ID>
    extends JsonSchemaForm, JsfVersioning, JsfStratrgyAware {

  JD getJsfData();

  void setJsfData(JD jsfData);

  default JD initJsfData() {
    return (JD) initJsfData(getFormType(), getFormBranch());
  }

  @SuppressWarnings({"unchecked"})
  default JD initJsfData(String formType, String formBranch) {
    return (JD) SpringBootUp.getBean(JsfService.class).newJsfData(formType, formBranch);
  }

  default JD _getJsfData() {
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
    Map<String, Object> formData =
        applyFormDataStrategy(new SimpleJsonSchemaForm(_getJsfData().getJsfSchema().getSchema(),
            _getJsfData().getJsfSchema().getUiSchema(), _getJsfData().getFormData()));

    Optional<JsfPatchService> jsfPatchServiceOpt = SpringBootUp.findBean(JsfPatchService.class);
    if (jsfPatchServiceOpt.isPresent()) {
      JsfPatchService jsfPatchService = jsfPatchServiceOpt.get();
      if (jsfPatchService.formDataPatch() != null) {
        formData = jsfPatchService.formDataPatch().apply(formData);
        return formData;
      }
    }

    return formData;
  }

  default void setFormData(Map<String, Object> formData) {
    _getJsfData().setFormData(formData);
  }

  default void setFormData(JsonObjectBase<?> formData) {
    _getJsfData().setFormData(formData.toMap());
  }

  default Map<String, Object> getSchema() {
    Map<String, Object> schema =
        applySchemaStrategy(new SimpleJsonSchemaForm(_getJsfData().getJsfSchema().getSchema(),
            _getJsfData().getJsfSchema().getUiSchema(), _getJsfData().getFormData()));

    Optional<JsfPatchService> jsfPatchServiceOpt = SpringBootUp.findBean(JsfPatchService.class);
    if (jsfPatchServiceOpt.isPresent()) {
      JsfPatchService jsfPatchService = jsfPatchServiceOpt.get();
      if (jsfPatchService.schemaPatch() != null) {
        schema = jsfPatchService.schemaPatch().apply(schema);
        return schema;
      }
    }

    return schema;
  }

  default void setSchema(Map<String, Object> schema) {
    _getJsfData().getJsfSchema().setSchema(schema);
  }

  default Map<String, Object> getUiSchema() {
    Map<String, Object> uiSchema =
        applyUiSchemaStrategy(new SimpleJsonSchemaForm(_getJsfData().getJsfSchema().getSchema(),
            _getJsfData().getJsfSchema().getUiSchema(), _getJsfData().getFormData()));

    Optional<JsfPatchService> jsfPatchServiceOpt = SpringBootUp.findBean(JsfPatchService.class);
    if (jsfPatchServiceOpt.isPresent()) {
      JsfPatchService jsfPatchService = jsfPatchServiceOpt.get();
      if (jsfPatchService.uiSchemaPatch() != null) {
        uiSchema = jsfPatchService.uiSchemaPatch().apply(uiSchema);
        return uiSchema;
      }
    }

    return uiSchema;
  }

  default void setUiSchema(Map<String, Object> uiSchema) {
    _getJsfData().getJsfSchema().setUiSchema(uiSchema);
  }

}
