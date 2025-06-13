package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.Iterator;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;

public class RjsfSchemaConverter {

  private static final ObjectMapper mapper = new ObjectMapper();

  /**
   * Merges RJSF v6 schema and uiSchema into a v5-compatible schema string.
   */
  @SneakyThrows
  public static String toRjsfV5Schema(String schema, String uiSchema) {
    JsonNode schemaNode = mapper.readTree(schema);
    JsonNode uiSchemaNode = mapper.readTree(uiSchema);

    // recursively merge enumNames
    mergeEnumNames(schemaNode, uiSchemaNode);

    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schemaNode);
  }

  @SneakyThrows
  public static JsonNode toRjsfV5Schema(JsonNode schemaNode, JsonNode uiSchemaNode) {
    // recursively merge enumNames
    mergeEnumNames(schemaNode, uiSchemaNode);

    return schemaNode;
  }

  /**
   * Recursively finds enum and attaches enumNames from uiSchema if available.
   */
  private static void mergeEnumNames(JsonNode schemaNode, JsonNode uiSchemaNode) {
    if (schemaNode == null || !schemaNode.isObject()) return;

    ObjectNode objSchema = (ObjectNode) schemaNode;

    // Attach enumNames if found in uiSchema
    if (schemaNode.has("enum") && uiSchemaNode != null && uiSchemaNode.isObject()) {
      JsonNode enumNamesNode = uiSchemaNode.get("ui:enumNames");
      if (enumNamesNode != null) {
        objSchema.set("enumNames", enumNamesNode);
      }
    }

    // Special handling for array items (common RJSF pattern)
    if (schemaNode.has("items")) {
      JsonNode itemsSchema = schemaNode.get("items");
      JsonNode itemsUiSchema =
          uiSchemaNode != null && uiSchemaNode.isObject() ? uiSchemaNode.get("items") : null;
      mergeEnumNames(itemsSchema, itemsUiSchema != null ? itemsUiSchema : uiSchemaNode);
    }

    // Recursively process object properties
    if (schemaNode.has("properties")) {
      JsonNode props = schemaNode.get("properties");
      if (props.isObject()) {
        Iterator<Map.Entry<String, JsonNode>> fields = props.properties().iterator();
        while (fields.hasNext()) {
          Map.Entry<String, JsonNode> entry = fields.next();
          String fieldName = entry.getKey();
          JsonNode childSchema = entry.getValue();
          JsonNode childUiSchema =
              uiSchemaNode != null && uiSchemaNode.isObject() ? uiSchemaNode.get(fieldName) : null;
          mergeEnumNames(childSchema, childUiSchema);
        }
      }
    }
  }

}
