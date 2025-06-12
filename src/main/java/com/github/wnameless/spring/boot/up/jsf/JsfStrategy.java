package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;

public interface JsfStrategy<F extends JsonSchemaForm> {

  default BooleanSupplier activeStatus() {
    return () -> true;
  }

  Class<F> getDocumentType();

  BiFunction<F, JsonSchemaForm, Map<String, Object>> schemaStrategy();

  BiFunction<F, JsonSchemaForm, Map<String, Object>> uiSchemaStrategy();

  BiFunction<F, JsonSchemaForm, Map<String, Object>> formDataStrategy();

  default BiFunction<F, JsonSchemaForm, JsonSchemaForm> wholeStrategy() {
    return null;
  }

}
