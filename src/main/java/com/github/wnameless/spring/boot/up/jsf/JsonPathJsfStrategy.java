package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.function.BiFunction;
import org.springframework.core.GenericTypeResolver;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;

public interface JsonPathJsfStrategy<F extends JsonSchemaForm> extends JsfStrategy<F> {

  @SuppressWarnings({"unchecked", "null"})
  default public Class<F> getDocumentType() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), JsfStrategy.class);
    return (Class<F>) genericTypeResolver[0];
  }

  BiFunction<F, DocumentContext, DocumentContext> jsonPathSchemaStrategy();

  default BiFunction<F, DocumentContext, DocumentContext> jsonPathUiSchemaStrategy() {
    return null;
  }

  default BiFunction<F, DocumentContext, DocumentContext> jsonPathFormDataStrategy() {
    return null;
  }

  default BiFunction<F, JsonSchemaForm, Map<String, Object>> schemaStrategy() {
    if (jsonPathSchemaStrategy() == null) return null;

    return (entity, jsf) -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getSchema());
      return jsonPathSchemaStrategy().apply(entity, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<F, JsonSchemaForm, Map<String, Object>> uiSchemaStrategy() {
    if (jsonPathUiSchemaStrategy() == null) return null;

    return (entity, jsf) -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getUiSchema());
      return jsonPathUiSchemaStrategy().apply(entity, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

  default BiFunction<F, JsonSchemaForm, Map<String, Object>> formDataStrategy() {
    if (jsonPathFormDataStrategy() == null) return null;

    return (entity, jsf) -> {
      DocumentContext docCtx = JsonPath.parse(jsf.getFormData());
      return jsonPathFormDataStrategy().apply(entity, docCtx).read("$",
          new TypeRef<Map<String, Object>>() {});
    };
  }

}
