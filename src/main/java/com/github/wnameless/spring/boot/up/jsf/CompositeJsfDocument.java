package com.github.wnameless.spring.boot.up.jsf;

import static java.util.stream.Collectors.toMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.model.JsfData;
import com.github.wnameless.spring.boot.up.jsf.model.JsfSchema;
import com.github.wnameless.spring.boot.up.jsf.service.JsfService;

public interface CompositeJsfDocument<JD extends JsfData<JS, ID>, JS extends JsfSchema<ID>, ID>
    extends JsonSchemaForm {

  default boolean isComposite() {
    return true;
  }

  List<CompositeFormPart> getCompositeFormParts();

  @SuppressWarnings({"unchecked"})
  default JD initJsfData(String formType, String formBranch) {
    return (JD) SpringBootUp.getBean(JsfService.class).newJsfData(formType, formBranch);
  }

  default Map<String, JD> getJsfDataPartsInternal() {
    Map<String, JD> jsfDataParts = getJsfDataParts();
    if (jsfDataParts == null) jsfDataParts = new LinkedHashMap<>();

    for (var cfp : getCompositeFormParts()) {
      JD jsonSchema = jsfDataParts.getOrDefault(cfp.formKeyStock().get(),
          initJsfData(cfp.formTypeStock().get(), cfp.formBranchStock().get()));
      jsfDataParts.put(cfp.formKeyStock().get(), jsonSchema);
    }
    setJsfDataParts(jsfDataParts);

    return jsfDataParts;
  }

  Map<String, JD> getJsfDataParts();

  void setJsfDataParts(Map<String, JD> jsfDataParts);

  default Map<String, JsonSchemaForm> getJsonSchemaFormParts() {
    return getJsfDataPartsInternal().entrySet().stream().collect(toMap(e -> e.getKey(), e -> {
      var jsf = new SimpleJsonSchemaForm();
      JD jd = e.getValue();
      jsf.setFormData(jd.getFormData());
      jsf.setSchema(jd.getJsfSchema().getSchema());
      jsf.setUiSchema(jd.getJsfSchema().getUiSchema());
      return jsf;
    }));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Map<String, JS> getJsfSchemaParts() {
    return getCompositeFormParts().stream().collect(toMap(cfp -> cfp.formKeyStock().get(), cfp -> {
      JsfService jsfService = SpringBootUp.getBean(JsfService.class);
      JS js = (JS) jsfService.findOrCreateBranchingJsfSchema(cfp.formTypeStock().get(),
          cfp.formBranchStock().get());
      return js;
    }));
  }

  @Override
  default Map<String, Object> getFormData() {
    return getJsfDataPartsInternal().entrySet().stream()
        .collect(toMap(e -> e.getKey(), e -> e.getValue().getFormData()));
  }

  @SuppressWarnings("unchecked")
  @Override
  default void setFormData(Map<String, Object> formData) {
    formData.forEach((k, v) -> {
      var jd = getJsfDataPartsInternal().get(k);
      if (jd != null) jd.setFormData((Map<String, Object>) v);
    });
  }

  @Override
  default Map<String, Object> getSchema() {
    var compositeSchema = new LinkedHashMap<String, Object>();
    compositeSchema.put("type", "object");
    compositeSchema.put("properties", getJsfDataPartsInternal().entrySet().stream()
        .collect(toMap(e -> e.getKey(), e -> e.getValue().getJsfSchema().getSchema())));
    return compositeSchema;
  }

  @SuppressWarnings("unchecked")
  @Override
  default void setSchema(Map<String, Object> schema) {
    var schemaParts = (Map<String, Map<String, Object>>) schema.get("properties");
    schemaParts.forEach((k, v) -> {
      var jd = getJsfDataPartsInternal().get(k);
      if (jd != null) jd.getJsfSchema().setSchema(v);
    });
  }

  @Override
  default Map<String, Object> getUiSchema() {
    return getJsfDataPartsInternal().entrySet().stream()
        .collect(toMap(e -> e.getKey(), e -> e.getValue().getJsfSchema().getUiSchema()));
  }

  @SuppressWarnings("unchecked")
  @Override
  default void setUiSchema(Map<String, Object> uiSchema) {
    uiSchema.forEach((k, v) -> {
      var jd = getJsfDataPartsInternal().get(k);
      if (jd != null) jd.getJsfSchema().setUiSchema((Map<String, Object>) v);
    });
  }

}
