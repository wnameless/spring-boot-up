package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

public interface JsonSchemaFormStrategy {

  default BooleanSupplier activeStatus() {
    return () -> true;
  }

  Class<? extends JsonSchemaForm> getDocumentType();

  Function<Map<String, Object>, Map<String, Object>> schemaStrategy();

  Function<Map<String, Object>, Map<String, Object>> uiSchemaStrategy();

  Function<Map<String, Object>, Map<String, Object>> formDataStrategy();

}
