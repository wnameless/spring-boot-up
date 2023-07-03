package com.github.wnameless.spring.boot.up.jsf;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.service.JsfService;

public interface JsfPOJO<T> extends JsonSchemaForm, JsfVersioning {

  T getPojo();

  void setPojo(T pojo);

  default Map<String, Object> getFormData() {
    T pojo = getPojo();
    if (pojo == null) {
      return new HashMap<>();
    } else {
      return SpringBootUp.getBean(ObjectMapper.class).convertValue(pojo,
          new TypeReference<Map<String, Object>>() {});
    }
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
    JsfService<?, ?, ?> jsfService = SpringBootUp.getBean(JsfService.class);
    return jsfService.getSchemaTemplate(getFormType());
  }

  default Map<String, Object> getUiSchema() {
    JsfService<?, ?, ?> jsfService = SpringBootUp.getBean(JsfService.class);
    return jsfService.getUiSchemaTemplate(getFormType());
  }

  default String getFormType() {
    return getClass().getSimpleName();
  }

  default String getFormBranch() {
    return JsfConfig.getDefaultBranchName();
  }

}
