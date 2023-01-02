package com.github.wnameless.spring.boot.up.lombok.extensions;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.wnameless.json.base.JacksonJsonValue;

public final class JacksonExtensions {

  private JacksonExtensions() {}

  public static Map<String, Object> valueToMap(ObjectMapper om, Object val) {
    return toMap(om.valueToTree(val));
  }

  public static ObjectNode asObject(JsonNode node) {
    return (ObjectNode) node;
  }

  public static ArrayNode asArray(JsonNode node) {
    return (ArrayNode) node;
  }

  public static Map<String, Object> toMap(JsonNode node) {
    return new ObjectMapper().convertValue(node, new TypeReference<Map<String, Object>>() {});
  }

  public static Map<String, Object> toMap(JsonNode node, ObjectMapper mapper) {
    return mapper.convertValue(node, new TypeReference<Map<String, Object>>() {});
  }

  public static List<Object> toList(JsonNode node) {
    return new ObjectMapper().convertValue(node, new TypeReference<List<Object>>() {});
  }

  public static List<Object> toList(JsonNode node, ObjectMapper mapper) {
    return mapper.convertValue(node, new TypeReference<List<Object>>() {});
  }

  public static JacksonJsonValue toJsonValue(JsonNode node) {
    return new JacksonJsonValue(node);
  }

}
