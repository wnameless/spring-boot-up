package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.Iterator;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;

/**
 * Converts RJSF v6-style schemas (with separate uiSchema) to a single, v5-compatible schema by
 * merging ui:enumNames into the main schema.
 */
public class RjsfSchemaConverter {

  private static final ObjectMapper mapper = new ObjectMapper();

  /**
   * Merges RJSF v6 schema and uiSchema into a v5-compatible schema string.
   */
  @SneakyThrows
  public static String toRjsfV5Schema(String schema, String uiSchema) {
    JsonNode schemaNode = mapper.readTree(schema);
    JsonNode uiSchemaNode = mapper.readTree(uiSchema);

    // Recursively merge enumNames
    mergeEnumNames(schemaNode, uiSchemaNode);

    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schemaNode);
  }

  /**
   * Merges RJSF v6 schema and uiSchema into a v5-compatible schema node.
   */
  @SneakyThrows
  public static JsonNode toRjsfV5Schema(JsonNode schemaNode, JsonNode uiSchemaNode) {
    // Recursively merge enumNames
    mergeEnumNames(schemaNode, uiSchemaNode);
    return schemaNode;
  }

  /**
   * Recursively traverses a schema and merges `ui:enumNames` from a parallel uiSchema wherever an
   * `enum` is found. This version handles nested properties, simple arrays, tuples (fixed arrays),
   * and combiner keywords (oneOf, allOf, anyOf).
   */
  private static void mergeEnumNames(JsonNode schemaNode, JsonNode uiSchemaNode) {
    // Base case: If the schema node is not a processable object, stop.
    if (schemaNode == null || !schemaNode.isObject()) {
      return;
    }

    ObjectNode objSchema = (ObjectNode) schemaNode;

    // Core Logic: If this schema level has an "enum", look for a corresponding "ui:enumNames"
    // in the uiSchema and merge it.
    if (schemaNode.has("enum") && uiSchemaNode != null && uiSchemaNode.isObject()) {
      JsonNode enumNamesNode = uiSchemaNode.get("ui:enumNames");
      if (enumNamesNode != null && enumNamesNode.isArray()) {
        objSchema.set("enumNames", enumNamesNode);
      }
    }

    // --- Recursive Traversals ---

    // FIX 1: Handle combiner keywords (oneOf, allOf, anyOf)
    final String[] COMBINER_KEYWORDS = {"oneOf", "allOf", "anyOf"};
    for (String keyword : COMBINER_KEYWORDS) {
      if (schemaNode.has(keyword) && schemaNode.get(keyword).isArray()) {
        for (JsonNode subSchema : schemaNode.get(keyword)) {
          // The uiSchema doesn't have a parallel oneOf/allOf/anyOf structure,
          // so we pass the *parent* uiSchemaNode down for the recursive call.
          mergeEnumNames(subSchema, uiSchemaNode);
        }
      }
    }

    // FIX 2: Handle array "items" (both simple arrays and tuples)
    if (schemaNode.has("items")) {
      JsonNode itemsSchema = schemaNode.get("items");
      JsonNode itemsUiSchema =
          uiSchemaNode != null && uiSchemaNode.isObject() ? uiSchemaNode.get("items") : null;

      if (itemsSchema.isObject()) {
        // Case A: Simple array (items is an object)
        // The ternary logic handles cases where ui:enumNames is at the top level of the array's UI
        // schema
        mergeEnumNames(itemsSchema, itemsUiSchema != null ? itemsUiSchema : uiSchemaNode);
      } else if (itemsSchema.isArray()) {
        // Case B: Tuple validation (items is an array)
        if (itemsUiSchema != null && itemsUiSchema.isArray()) {
          for (int i = 0; i < itemsSchema.size(); i++) {
            if (i < itemsUiSchema.size()) { // Safely access corresponding uiSchema
              mergeEnumNames(itemsSchema.get(i), itemsUiSchema.get(i));
            }
          }
        }
      }
    }

    // Handle nested object properties
    if (schemaNode.has("properties")) {
      JsonNode props = schemaNode.get("properties");
      if (props.isObject()) {
        Iterator<Map.Entry<String, JsonNode>> fields = props.properties().iterator();
        while (fields.hasNext()) {
          Map.Entry<String, JsonNode> entry = fields.next();
          String fieldName = entry.getKey();
          JsonNode childSchema = entry.getValue();
          // Find the corresponding field in the uiSchema for the recursive call
          JsonNode childUiSchema =
              uiSchemaNode != null && uiSchemaNode.isObject() ? uiSchemaNode.get(fieldName) : null;
          mergeEnumNames(childSchema, childUiSchema);
        }
      }
    }
  }

}
