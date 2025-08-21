package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsfDataUtils {

  public Map<String, Object> mergeData(Map<String, Object> data,
      Map<String, Object> additionalData) {
    return mergeData(data, additionalData, false);
  }

  public Map<String, Object> mergeData(Map<String, Object> data,
      Map<String, Object> additionalData, boolean overrideArray) {
    return mergeSchema(data, additionalData, overrideArray);
  }

  public Map<String, Object> mergeSchema(Map<String, Object> schema,
      Map<String, Object> additionalData) {
    return mergeSchema(schema, additionalData, false);
  }

  public Map<String, Object> mergeSchema(Map<String, Object> schema,
      Map<String, Object> additionalData, boolean overrideArray) {
    var mapperOpt = SpringBootUp.findBean(ObjectMapper.class);
    ObjectMapper mapper = mapperOpt.orElse(new ObjectMapper());
    ObjectNode baseNode = mapper.convertValue(schema, ObjectNode.class);
    ObjectNode overrideNode = mapper.convertValue(additionalData, ObjectNode.class);

    merge(baseNode, overrideNode, overrideArray);

    return mapper.convertValue(baseNode, new TypeReference<Map<String, Object>>() {});
  }

  private static void merge(ObjectNode baseNode, ObjectNode overrideNode, boolean overrideArray) {
    overrideNode.fieldNames().forEachRemaining(field -> {
      var baseValue = baseNode.get(field);
      var overrideValue = overrideNode.get(field);

      if (baseValue != null && baseValue.isObject() && overrideValue.isObject()) {
        merge((ObjectNode) baseValue, (ObjectNode) overrideValue, overrideArray);
      } else if (baseValue != null && baseValue.isArray() && overrideValue.isArray()) {
        if (overrideArray) {
          baseNode.set(field, overrideValue);
        } else {
          ArrayNode mergedArray = ((ObjectNode) baseNode).arrayNode();
          mergedArray.addAll((ArrayNode) baseValue);
          mergedArray.addAll((ArrayNode) overrideValue);
          baseNode.set(field, mergedArray);
        }
      } else {
        baseNode.set(field, overrideValue);
      }
    });
  }

}
