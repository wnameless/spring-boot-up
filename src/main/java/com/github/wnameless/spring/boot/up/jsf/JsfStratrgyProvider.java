package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.Optional;
import com.github.wnameless.spring.boot.up.SpringBootUp;

public interface JsfStratrgyProvider {

  @SuppressWarnings("rawtypes")
  default Optional<JsfStrategy> getJsonSchemaFormStrategy() {
    return SpringBootUp.getBeansOfType(JsfStrategy.class).values().stream()
        .filter(dc -> dc.getDocumentType().equals(this.getClass()))
        .filter(dc -> dc.activeStatus().getAsBoolean()).findFirst();
  }

  default Optional<JsfDefaultEnumStrategy> getJsfDefaultEnumStategy() {
    return SpringBootUp.getBeansOfType(JsfDefaultEnumStrategy.class).values().stream().findFirst();
  }

  @SuppressWarnings("unchecked")
  default Map<String, Object> applySchemaStrategy(JsonSchemaForm jsf) {
    var defaultEnumStategyOpt = getJsfDefaultEnumStategy();
    if (defaultEnumStategyOpt.isPresent()) {
      jsf = defaultEnumStategyOpt.get().applyDefaultEnumStategy(this, jsf);
    }

    var documentStrategy = getJsonSchemaFormStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().schemaStrategy() != null) {
      return (Map<String, Object>) documentStrategy.get().schemaStrategy().apply(this, jsf);
    }
    return jsf.getSchema();
  }

  @SuppressWarnings("unchecked")
  default Map<String, Object> applyUiSchemaStrategy(JsonSchemaForm jsf) {
    var documentStrategy = getJsonSchemaFormStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().uiSchemaStrategy() != null) {
      return (Map<String, Object>) documentStrategy.get().uiSchemaStrategy().apply(this, jsf);
    }
    return jsf.getUiSchema();
  }

  @SuppressWarnings("unchecked")
  default Map<String, Object> applyFormDataStrategy(JsonSchemaForm jsf) {
    var documentStrategy = getJsonSchemaFormStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().formDataStrategy() != null) {
      return (Map<String, Object>) documentStrategy.get().formDataStrategy().apply(this, jsf);
    }
    return jsf.getFormData();
  }

}
