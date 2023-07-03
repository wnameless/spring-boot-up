package com.github.wnameless.spring.boot.up.jsf;

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
    return SpringBootUp.getBean(ObjectMapper.class).convertValue(pojo,
        new TypeReference<Map<String, Object>>() {});
  }

  default void setFormData(Map<String, Object> formData) {
    T pojo = getPojo();
    pojo = (T) SpringBootUp.getBean(ObjectMapper.class).convertValue(formData, pojo.getClass());
    setPojo(pojo);
  }

  default Map<String, Object> getSchema() {
    return SpringBootUp.getBean(JsfService.class).getSchemaTemplate(getFormType());
  }

  default Map<String, Object> getUiSchema() {
    return SpringBootUp.getBean(JsfService.class).getUiSchemaTemplate(getFormType());
  }

  default String getFormType() {
    return getClass().getSimpleName();
  }

  default String getFormBranch() {
    return JsfConfig.getDefaultBranchName();
  }

}
