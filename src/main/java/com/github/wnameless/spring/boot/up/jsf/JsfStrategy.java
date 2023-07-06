package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public interface JsfStrategy {

  default BooleanSupplier activeStatus() {
    return () -> true;
  }

  Class<? extends JsonSchemaForm> getDocumentType();

  Function<JsonSchemaForm, Map<String, Object>> schemaStrategy();

  Function<JsonSchemaForm, Map<String, Object>> uiSchemaStrategy();

  Function<JsonSchemaForm, Map<String, Object>> formDataStrategy();

}
