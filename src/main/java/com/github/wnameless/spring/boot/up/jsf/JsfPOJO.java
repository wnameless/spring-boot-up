package com.github.wnameless.spring.boot.up.jsf;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.service.JsfPOJOService;
import com.github.wnameless.spring.boot.up.jsf.service.JsfPatchService;

public interface JsfPOJO<T, ID> extends JsonSchemaForm, JsfVersioning, JsfStratrgyAware {

  static final Logger log = LoggerFactory.getLogger(JsfPOJO.class);

  ID getId();

  T getPojo();

  void setPojo(T pojo);

  default void setPojoWithPopulation(T pojo) {
    setPojo(pojo);
    populate();
  }

  @SuppressWarnings({"unchecked"})
  default void populate() {
    T pojo = getPojo();
    if (pojo == null) {
      log.warn("Empty POJO");
      return;
    }

    String[] names = SpringBootUp.applicationContext().getBeanNamesForType(ResolvableType
        .forClassWithGenerics(JsfPOJOConverter.class, pojo.getClass(), this.getClass()));
    if (names.length > 0) {
      var converter = SpringBootUp.getBean(names[0], JsfPOJOConverter.class);
      converter.map(pojo, this);
    } else {
      log.warn("POJO Converter for " + pojo.getClass().getSimpleName() + " not found");
    }
  }

  default Map<String, Object> _getFormData() {
    T pojo = getPojo();
    if (pojo == null) {
      return new HashMap<>();
    } else {
      return SpringBootUp.getBean(ObjectMapper.class).convertValue(pojo,
          new TypeReference<Map<String, Object>>() {});
    }
  }

  default Map<String, Object> getFormData() {
    JsfPOJOService jsfPojoService = SpringBootUp.getBean(JsfPOJOService.class);
    Map<String, Object> formData = applyFormDataStrategy(
        new SimpleJsonSchemaForm(jsfPojoService.getSchemaTemplate(getFormType()),
            jsfPojoService.getUiSchemaTemplate(getFormType()), _getFormData()));

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

  @SuppressWarnings("unchecked")
  default void setFormData(Map<String, Object> formData) {
    T pojo = getPojo();
    if (pojo != null) {
      ObjectMapper objectMapper = SpringBootUp.getBean(ObjectMapper.class);
      pojo = (T) objectMapper.convertValue(formData, pojo.getClass());
      setPojo(pojo);
    }
  }

  default Map<String, Object> getSchema() {
    JsfPOJOService jsfPojoService = SpringBootUp.getBean(JsfPOJOService.class);
    Map<String, Object> schema = applySchemaStrategy(
        new SimpleJsonSchemaForm(jsfPojoService.getSchemaTemplate(getFormType()),
            jsfPojoService.getUiSchemaTemplate(getFormType()), _getFormData()));

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

  default Map<String, Object> getUiSchema() {
    JsfPOJOService jsfPojoService = SpringBootUp.getBean(JsfPOJOService.class);
    Map<String, Object> uiSchema = applyUiSchemaStrategy(
        new SimpleJsonSchemaForm(jsfPojoService.getSchemaTemplate(getFormType()),
            jsfPojoService.getUiSchemaTemplate(getFormType()), _getFormData()));

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

  default String getFormType() {
    return getClass().getSimpleName();
  }

  default String getFormBranch() {
    return JsfConfig.getDefaultBranchName();
  }

}
