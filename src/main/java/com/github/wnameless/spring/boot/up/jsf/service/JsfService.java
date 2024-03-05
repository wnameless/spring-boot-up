package com.github.wnameless.spring.boot.up.jsf.service;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.GenericTypeResolver;
import com.github.wnameless.spring.boot.up.jsf.JsfConfig;
import com.github.wnameless.spring.boot.up.jsf.JsonCoreFactory;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaFormUtils;
import com.github.wnameless.spring.boot.up.jsf.model.JsfData;
import com.github.wnameless.spring.boot.up.jsf.model.JsfSchema;
import com.github.wnameless.spring.boot.up.jsf.repository.JsfDataRepository;
import com.github.wnameless.spring.boot.up.jsf.repository.JsfSchemaRepository;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import lombok.SneakyThrows;

public interface JsfService<JD extends JsfData<JS, ID>, JS extends JsfSchema<ID>, ID> {

  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JsfService.class);

  JsfSchemaRepository<JS, ID> getJsfSchemaRepository();

  JsfDataRepository<JD, JS, ID> getJsfDataRepository();

  @SneakyThrows
  @SuppressWarnings("unchecked")
  default JS newJsfSchema() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), JsfService.class);
    return (JS) genericTypeResolver[1].getDeclaredConstructor().newInstance();
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  default JD newJsfData() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), JsfService.class);
    return (JD) genericTypeResolver[0].getDeclaredConstructor().newInstance();
  }

  default JS createJsfSchema(String formType, Map<String, Object> schema,
      Map<String, Object> uiSchema) {
    JS js = newJsfSchema();
    js.setFormType(formType);
    js.setSchema(schema);
    js.setUiSchema(uiSchema);
    js.setVersion(LocalDateTime.now());

    return getJsfSchemaRepository().save(js);
  }

  default JS findJsfSchema(String formType) {
    return getJsfSchemaRepository().findFirstByFormTypeAndFormBranchOrderByVersionDesc(formType,
        JsfConfig.getDefaultBranchName());
  }

  default JS findOrCreateJsfSchema(String formType) {
    JS js = findJsfSchema(formType);
    if (js != null) return js;

    return createJsfSchema(formType, getSchemaTemplate(formType), getUiSchemaTemplate(formType));
  }

  default String getTemplatePath() {
    return "jsf-templates";
  }

  default Map<String, Object> getSchemaTemplate(String formType) {
    try {
      URL schemaUrl = Resources
          .getResource(getTemplatePath() + "/" + formType + "/" + formType + ".schema.json");
      @SuppressWarnings("null")
      String json = Resources.toString(schemaUrl, Charsets.UTF_8);
      return JsonCoreFactory.INSTANCE.readJson(json).asObject().toMap();
    } catch (Exception e) {
      log.info("Schema template not found", e);
      return JsonSchemaFormUtils.defaultSchema();
    }
  }

  default Map<String, Object> getUiSchemaTemplate(String formType) {
    try {
      URL schemaUrl = Resources
          .getResource(getTemplatePath() + "/" + formType + "/" + formType + ".uiSchema.json");
      @SuppressWarnings("null")
      String json = Resources.toString(schemaUrl, Charsets.UTF_8);
      return JsonCoreFactory.INSTANCE.readJson(json).asObject().toMap();
    } catch (Exception e) {
      log.info("UiSchema template not found", e);
      return JsonSchemaFormUtils.defaultUiSchema();
    }
  }

  default JS createBranchingJsfSchema(String formType, String formBranch) {
    JS upstream = findOrCreateJsfSchema(formType);
    if (formBranch.equals(JsfConfig.getDefaultBranchName())) {
      return upstream;
    }

    JS js = newJsfSchema();
    js.setFormType(formType);
    js.setFormBranch(formBranch);
    js.setVersion(LocalDateTime.now());
    js.setSchema(upstream.getSchema());
    js.setUiSchema(upstream.getUiSchema());

    return getJsfSchemaRepository().save(js);
  }

  default JS findBreachingJsfSchema(String formType, String formBranch) {
    if (StringUtils.isBlank(formBranch)) {
      return getJsfSchemaRepository().findFirstByFormTypeAndFormBranchOrderByVersionDesc(formType,
          JsfConfig.getDefaultBranchName());
    }
    return getJsfSchemaRepository().findFirstByFormTypeAndFormBranchOrderByVersionDesc(formType,
        formBranch);
  }

  default JS findOrCreateBranchingJsfSchema(String formType, String formBranch) {
    JS js = findBreachingJsfSchema(formType, formBranch);
    if (js != null) return js;

    return createBranchingJsfSchema(formType, formBranch);
  }

  default JD newJsfData(String formType, String formBranch) {
    JS js = findOrCreateBranchingJsfSchema(formType, formBranch);

    JD jd = newJsfData();
    jd.setFormData(JsonSchemaFormUtils.defaultFormData());
    jd.setJsfSchema(js);
    jd.setVersion(LocalDateTime.now());

    return jd;
  }

}
