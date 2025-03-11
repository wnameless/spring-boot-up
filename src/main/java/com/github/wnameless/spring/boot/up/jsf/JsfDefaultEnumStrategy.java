package com.github.wnameless.spring.boot.up.jsf;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;

public interface JsfDefaultEnumStrategy {

  Map<Class<? extends JsonSchemaForm>, List<JsfDefaultEnum>> getJsfDefaultEnums();

  default JsonSchemaForm applyDefaultEnumStategy(JsfStratrgyProvider entity, JsonSchemaForm jsf) {
    var defaultEnums =
        Optional.ofNullable(getJsfDefaultEnums().get(entity.getClass())).orElse(List.of());
    if (defaultEnums.isEmpty()) return jsf;

    DocumentContext docCtx = JsonPath.parse(jsf.getSchema());

    for (var de : defaultEnums) {
      docCtx.put(de.getEnumPath(), "enum", de.getEnum());
      docCtx.put(de.getEnumPath(), "enumNames", de.getEnumNames());
    }

    jsf.setSchema(docCtx.read("$", new TypeRef<Map<String, Object>>() {}));
    return jsf;
  }

}
