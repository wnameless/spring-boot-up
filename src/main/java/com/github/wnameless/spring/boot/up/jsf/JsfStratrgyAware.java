package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.Optional;
import com.github.wnameless.spring.boot.up.SpringBootUp;

public interface JsfStratrgyAware {

  default Optional<JsfStrategy> getJsonSchemaFormStrategy() {
    return SpringBootUp.getBeansOfType(JsfStrategy.class).values().stream()
        .filter(dc -> dc.getDocumentType().equals(this.getClass()))
        .filter(dc -> dc.activeStatus().getAsBoolean()).findFirst();
  }

  default Map<String, Object> applySchemaStrategy(JsonSchemaForm jsf) {
    var documentStrategy = getJsonSchemaFormStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().schemaStrategy() != null) {
      return documentStrategy.get().schemaStrategy().apply(jsf);
    }
    return jsf.getSchema();
  }

  default Map<String, Object> applyUiSchemaStrategy(JsonSchemaForm jsf) {
    var documentStrategy = getJsonSchemaFormStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().uiSchemaStrategy() != null) {
      return documentStrategy.get().uiSchemaStrategy().apply(jsf);
    }
    return jsf.getUiSchema();
  }

  default Map<String, Object> applyFormDataStrategy(JsonSchemaForm jsf) {
    var documentStrategy = getJsonSchemaFormStrategy();
    if (documentStrategy.isPresent() && documentStrategy.get().formDataStrategy() != null) {
      return documentStrategy.get().formDataStrategy().apply(jsf);
    }
    return jsf.getFormData();
  }

}
