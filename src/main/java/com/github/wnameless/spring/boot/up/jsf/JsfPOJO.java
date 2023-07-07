package com.github.wnameless.spring.boot.up.jsf;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.service.JsfPOJOService;

public interface JsfPOJO<T> extends JsonSchemaForm, JsfVersioning, JsfStratrgyAware {

  T getPojo();

  void setPojo(T pojo);

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
    return applyFormDataStrategy(
        new SimpleJsonSchemaForm(jsfPojoService.getSchemaTemplate(getFormType()),
            jsfPojoService.getUiSchemaTemplate(getFormType()), _getFormData()));
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
    return applySchemaStrategy(
        new SimpleJsonSchemaForm(jsfPojoService.getSchemaTemplate(getFormType()),
            jsfPojoService.getUiSchemaTemplate(getFormType()), _getFormData()));
  }

  default Map<String, Object> getUiSchema() {
    JsfPOJOService jsfPojoService = SpringBootUp.getBean(JsfPOJOService.class);
    return applyUiSchemaStrategy(
        new SimpleJsonSchemaForm(jsfPojoService.getSchemaTemplate(getFormType()),
            jsfPojoService.getUiSchemaTemplate(getFormType()), _getFormData()));
  }

  default String getFormType() {
    return getClass().getSimpleName();
  }

  default String getFormBranch() {
    return JsfConfig.getDefaultBranchName();
  }

}