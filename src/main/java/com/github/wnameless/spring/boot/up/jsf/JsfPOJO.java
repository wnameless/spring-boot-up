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

public interface JsfPOJO<T> extends JsonSchemaForm, JsfVersioning, JsfStratrgyProvider {

  static final Logger log = LoggerFactory.getLogger(JsfPOJO.class);

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

    // Priority 1: MapStruct converter (preferred - compile-time generated, faster)
    String[] mapStructNames = SpringBootUp.applicationContext().getBeanNamesForType(ResolvableType
        .forClassWithGenerics(MapStructJsfPOJOConverter.class, pojo.getClass(), this.getClass()));
    if (mapStructNames.length > 0) {
      var converter = SpringBootUp.getBean(mapStructNames[0], MapStructJsfPOJOConverter.class);
      converter.mapToJsfPOJO(pojo, this);
      return;
    }

    // Priority 2: Legacy ModelMapper-based converter (deprecated)
    @SuppressWarnings("deprecation")
    String[] legacyNames = SpringBootUp.applicationContext().getBeanNamesForType(ResolvableType
        .forClassWithGenerics(JsfPOJOConverter.class, pojo.getClass(), this.getClass()));
    if (legacyNames.length > 0) {
      @SuppressWarnings("deprecation")
      var converter = SpringBootUp.getBean(legacyNames[0], JsfPOJOConverter.class);
      converter.map(pojo, this);
      return;
    }

    // Priority 3: Direct ModelMapper (fallback)
    log.warn("Default ModelMapper is used for mapping because no converter for "
        + pojo.getClass().getSimpleName() + " was found");
    JsfConfig.getModelMapper().map(pojo, this);
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
    var jsf = new SimpleJsonSchemaForm(jsfPojoService.getSchemaTemplate(getFormType()),
        jsfPojoService.getUiSchemaTemplate(getFormType()), _getFormData());
    Map<String, Object> formData = applyFormDataStrategy(jsf);
    jsf.setFormData(formData);

    Optional<JsfPatchService> jsfPatchServiceOpt = SpringBootUp.findBean(JsfPatchService.class);
    if (jsfPatchServiceOpt.isPresent()) {
      JsfPatchService jsfPatchService = jsfPatchServiceOpt.get();
      if (jsfPatchService.wholePatch() != null) {
        formData = jsfPatchService.wholePatch().apply(jsf).getFormData();
        return formData;
      } else if (jsfPatchService.formDataPatch() != null) {
        formData = jsfPatchService.formDataPatch().apply(jsf);
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
    var jsf = new SimpleJsonSchemaForm(jsfPojoService.getSchemaTemplate(getFormType()),
        jsfPojoService.getUiSchemaTemplate(getFormType()), _getFormData());
    Map<String, Object> schema = applySchemaStrategy(jsf);
    jsf.setSchema(schema);

    Optional<JsfPatchService> jsfPatchServiceOpt = SpringBootUp.findBean(JsfPatchService.class);
    if (jsfPatchServiceOpt.isPresent()) {
      JsfPatchService jsfPatchService = jsfPatchServiceOpt.get();
      if (jsfPatchService.wholePatch() != null) {
        schema = jsfPatchService.wholePatch().apply(jsf).getSchema();
        return schema;
      } else if (jsfPatchService.schemaPatch() != null) {
        schema = jsfPatchService.schemaPatch().apply(jsf);
        return schema;
      }
    }

    return schema;
  }

  default Map<String, Object> getUiSchema() {
    JsfPOJOService jsfPojoService = SpringBootUp.getBean(JsfPOJOService.class);
    var jsf = new SimpleJsonSchemaForm(jsfPojoService.getSchemaTemplate(getFormType()),
        jsfPojoService.getUiSchemaTemplate(getFormType()), _getFormData());
    Map<String, Object> uiSchema = applyUiSchemaStrategy(jsf);
    jsf.setUiSchema(uiSchema);

    Optional<JsfPatchService> jsfPatchServiceOpt = SpringBootUp.findBean(JsfPatchService.class);
    if (jsfPatchServiceOpt.isPresent()) {
      JsfPatchService jsfPatchService = jsfPatchServiceOpt.get();
      if (jsfPatchService.wholePatch() != null) {
        uiSchema = jsfPatchService.wholePatch().apply(jsf).getUiSchema();
        return uiSchema;
      } else if (jsfPatchService.uiSchemaPatch() != null) {
        uiSchema = jsfPatchService.uiSchemaPatch().apply(jsf);
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
