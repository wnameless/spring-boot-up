package com.github.wnameless.spring.boot.up.jsf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.wnameless.spring.boot.up.jsf.util.JsfDisplayUtils;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.TypeRef;
import net.sf.rubycollect4j.Ruby;

public interface JsfDefaultEnumStrategy {

  Map<Class<? extends JsonSchemaForm>, List<JsfDefaultEnum>> getJsfDefaultEnums();

  default JsonSchemaForm applyDefaultEnumStategy(JsfStratrgyProvider entity, JsonSchemaForm jsf) {
    var defaultEnums =
        Optional.ofNullable(getJsfDefaultEnums().get(entity.getClass())).orElse(List.of());
    if (defaultEnums.isEmpty()) return jsf;

    DocumentContext docCtx = JsonPath.parse(jsf.getSchema());
    DocumentContext uiDocCtx = JsonPath.parse(jsf.getUiSchema());

    for (var de : defaultEnums) {
      if (de.getIfConditions().isEmpty()) {
        JsfDisplayUtils.setEnum(docCtx, de.getEnumPath(), de.getEnum(), de.getEnumNames());
        JsfDisplayUtils.setUiEnumNames(uiDocCtx, de.getEnumPath(), de.getEnumNames());
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
            JsfDisplayUtils.setUiEnumNames(uiDocCtx, ifJsonPath, de.getEnumNames());
          }

          allOf.add(mapper.valueToTree(Map.of("if", ifRoot, "then", thenRoot)));
        }

        boolean hasAllOf = false;
        try {
          docCtx.read("$.allOf", new TypeRef<List<Object>>() {});
          hasAllOf = true;
        } catch (PathNotFoundException e) {
          hasAllOf = false;
        }
        var newAllOf = mapper.convertValue(allOf, new TypeReference<List<Object>>() {});
        if (hasAllOf) {
          var oldAllOf = docCtx.read("$.allOf", new TypeRef<List<Object>>() {});
          var totalAllOf = new ArrayList<Object>();
          totalAllOf.addAll(oldAllOf);
          totalAllOf.addAll(newAllOf);
          docCtx.put("$", "allOf", totalAllOf);
        } else {
          docCtx.put("$", "allOf", newAllOf);
        }
      }
    }

    jsf.setSchema(docCtx.read("$", new TypeRef<Map<String, Object>>() {}));
    jsf.setUiSchema(uiDocCtx.read("$", new TypeRef<Map<String, Object>>() {}));
    return jsf;
  }

}
