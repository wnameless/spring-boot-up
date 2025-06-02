package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsfSchemaUtils {

  public List<JsfSimpleField> schemaToFields(Map<String, Object> jsonSchema) {
    return schemaToFields(jsonSchema, false);
  }

  public List<JsfSimpleField> schemaToFields(Map<String, Object> jsonSchema,
      boolean keyAsMissingTitle) {
    String json;
    try {
      ObjectMapper mapper = new ObjectMapper();
      json = mapper.writeValueAsString(jsonSchema);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to parse schema", e);
    }
    return schemaToFields(json, keyAsMissingTitle);
  }

  public List<JsfSimpleField> schemaToFields(String jsonSchema) {
    return schemaToFields(jsonSchema, false);
  }

  public List<JsfSimpleField> schemaToFields(String jsonSchema, boolean keyAsMissingTitle) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(jsonSchema);
      List<JsfSimpleField> fields = new ArrayList<>();
      traverseSchema(root, "", "$", false, new ArrayList<>(), "", fields, keyAsMissingTitle);
      return fields;
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse schema", e);
    }
  }

  private void traverseSchema(JsonNode schemaNode, String flattenedPrefix, String jsonPathPrefix,
      boolean parentIsArray, List<String> parentTitles, String currentPropName,
      List<JsfSimpleField> fields, boolean keyAsMissingTitle) {
    String type = getType(schemaNode);
    List<String> currentTitles = new ArrayList<>(parentTitles);

    boolean hasTitle = schemaNode.has("title") && !schemaNode.get("title").asText().isEmpty();
    String label;

    if (hasTitle) {
      label = schemaNode.get("title").asText();
    } else if (keyAsMissingTitle && currentPropName != null && !currentPropName.isEmpty()) {
      label = currentPropName;
    } else {
      label = JsfSimpleField.TITLE_PLACEHOLDER;
    }
    currentTitles.add(label);

    if ("object".equals(type)) {
      JsonNode props = schemaNode.get("properties");
      if (props != null && props.isObject()) {
        Iterator<String> fieldNames = props.fieldNames();
        while (fieldNames.hasNext()) {
          String propName = fieldNames.next();
          JsonNode propSchema = props.get(propName);
          String nextFlat = flattenedPrefix.isEmpty() ? propName : flattenedPrefix + "." + propName;
          String nextJsonPath = jsonPathPrefix + ".properties." + propName;
          traverseSchema(propSchema, nextFlat, nextJsonPath, false, currentTitles, propName, fields,
              keyAsMissingTitle);
        }
      }
    } else if ("array".equals(type)) {
      JsonNode items = schemaNode.get("items");
      String nextFlat = flattenedPrefix;
      String nextJsonPath = jsonPathPrefix + ".items";
      // For arrays, currentPropName not updated
      traverseSchema(items, nextFlat, nextJsonPath, true, currentTitles, currentPropName, fields,
          keyAsMissingTitle);
    } else {
      fields.add(new JsfSimpleField(currentTitles, flattenedPrefix, jsonPathPrefix, parentIsArray,
          hasTitle));
    }
  }

  private String getType(JsonNode schemaNode) {
    JsonNode typeNode = schemaNode.get("type");
    if (typeNode == null) return null;
    if (typeNode.isTextual()) return typeNode.asText();
    if (typeNode.isArray() && typeNode.size() > 0) return typeNode.get(0).asText();
    return null;
  }

}
