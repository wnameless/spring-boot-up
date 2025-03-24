package com.github.wnameless.spring.boot.up.jsf;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import net.sf.rubycollect4j.Ruby;

public interface JsfDefaultEnumStrategy {

  Map<Class<? extends JsonSchemaForm>, List<JsfDefaultEnum>> getJsfDefaultEnums();

  default JsonSchemaForm applyDefaultEnumStategy(JsfStratrgyProvider entity, JsonSchemaForm jsf) {
    var defaultEnums =
        Optional.ofNullable(getJsfDefaultEnums().get(entity.getClass())).orElse(List.of());
    if (defaultEnums.isEmpty()) return jsf;

    DocumentContext docCtx = JsonPath.parse(jsf.getSchema());

    for (var de : defaultEnums) {
      if (de.getIfConditions().isEmpty()) {
        docCtx.put(de.getEnumPath(), "enum", de.getEnum());
        docCtx.put(de.getEnumPath(), "enumNames", de.getEnumNames());
      } else {
        ObjectMapper mapper = new ObjectMapper();
        var allOf = mapper.createArrayNode();
        for (var ifCond : de.getIfConditions()) {
          ObjectNode ifRoot = mapper.createObjectNode();
          ObjectNode ifCurrent = null;
          var ifJsonPath = ifCond.getJsonPath();
          var ifPathParts = Ruby.Array.copyOf(ifJsonPath.split("\\."));
          ifPathParts.shift();
          for (var part : ifPathParts) {
            if (ifCurrent == null) {
              ifCurrent = ifRoot.withObject(part);
            } else {
              ifCurrent = ifCurrent.withObject(part);
            }
          }
          if (ifCurrent != null) {
            ifCurrent.set("const", mapper.valueToTree(ifCond.getConst()));
          }

          ObjectNode thenRoot = mapper.createObjectNode();
          ObjectNode thenCurrent = null;
          var thenJsonPath = de.getEnumPath();
          var thenPathParts = Ruby.Array.copyOf(thenJsonPath.split("\\."));
          thenPathParts.shift();
          for (var part : thenPathParts) {
            if (thenCurrent == null) {
              thenCurrent = thenRoot.withObject(part);
            } else {
              thenCurrent = thenCurrent.withObject(part);
            }
          }
          if (thenCurrent != null) {
            thenCurrent.set("enum", mapper.valueToTree(de.getEnum()));
            thenCurrent.set("enumNames", mapper.valueToTree(de.getEnumNames()));
          }

          allOf.add(mapper.valueToTree(Map.of("if", ifRoot, "then", thenRoot)));
        }

        docCtx.put("$", "allOf", mapper.convertValue(allOf, new TypeReference<List<Object>>() {}));
      }
    }

    jsf.setSchema(docCtx.read("$", new TypeRef<Map<String, Object>>() {}));
    return jsf;
  }

}
