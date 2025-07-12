package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.Iterator;
import java.util.Map;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.SneakyThrows;

/**
 * Converts RJSF v6-style schemas (with a separate uiSchema) to a single, v5-compatible schema. It
 * merges UI properties like "ui:title", "ui:description", and "ui:enumNames" directly into the main
 * schema.
 */
public class RjsfSchemaConverter {

  private static final ObjectMapper mapper = new ObjectMapper();

  /**
   * Merges an RJSF v6 schema and uiSchema into a v5-compatible schema string.
   *
   * @param schema The JSON schema string.
   * @param uiSchema The UI schema string.
   * @return A pretty-printed, v5-compatible JSON schema string.
   */
  @SneakyThrows
  public static String toRjsfV5Schema(String schema, String uiSchema) {
    JsonNode schemaNode = mapper.readTree(schema);
    JsonNode uiSchemaNode = mapper.readTree(uiSchema);

    // Recursively merge UI properties
    mergeUiSchemaIntoSchema(schemaNode, uiSchemaNode);

    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(schemaNode);
  }

  /**
   * Merges an RJSF v6 schema and uiSchema into a v5-compatible schema node.
   *
   * @param schemaNode The JSON schema as a JsonNode.
   * @param uiSchemaNode The UI schema as a JsonNode.
   * @return A v5-compatible JSON schema as a JsonNode.
   */
  @SneakyThrows
  public static JsonNode toRjsfV5Schema(JsonNode schemaNode, JsonNode uiSchemaNode) {
    // Recursively merge UI properties
    mergeUiSchemaIntoSchema(schemaNode, uiSchemaNode);
    return schemaNode;
  }

  /**
   * Recursively traverses a schema and merges `ui:title`, `ui:description`, and `ui:enumNames` from
   * a parallel uiSchema. This method handles nested properties, arrays (simple and tuple-based),
   * and combiner keywords (oneOf, allOf, anyOf).
   *
   * @param schemaNode The current node of the JSON schema to process.
   * @param uiSchemaNode The corresponding node in the UI schema.
   */
  private static void mergeUiSchemaIntoSchema(JsonNode schemaNode, JsonNode uiSchemaNode) {
    // Base case: Stop if the schema node is not a processable object.
    if (schemaNode == null || !schemaNode.isObject() || uiSchemaNode == null
        || !uiSchemaNode.isObject()) {
      return;
    }

    ObjectNode objSchema = (ObjectNode) schemaNode;

    // --- Core Merging Logic ---
    // Merge "ui:title" into "title"
    if (uiSchemaNode.has("ui:title")) {
      objSchema.set("title", uiSchemaNode.get("ui:title"));
    }

    // Merge "ui:description" into "description"
    if (uiSchemaNode.has("ui:description")) {
      objSchema.set("description", uiSchemaNode.get("ui:description"));
    }

    // Merge "ui:enumNames" into "enumNames" if "enum" is present
    if (schemaNode.has("enum")) {
      JsonNode enumNamesNode = uiSchemaNode.get("ui:enumNames");
      if (enumNamesNode != null && enumNamesNode.isArray()) {
        objSchema.set("enumNames", enumNamesNode);
      }
    }

    // --- Recursive Traversals ---

    // Handle combiner keywords (oneOf, allOf, anyOf)
    final String[] COMBINER_KEYWORDS = {"oneOf", "allOf", "anyOf"};
    for (String keyword : COMBINER_KEYWORDS) {
      if (schemaNode.has(keyword) && schemaNode.get(keyword).isArray()) {
        for (JsonNode subSchema : schemaNode.get(keyword)) {
          // The uiSchema doesn't have a parallel oneOf/allOf/anyOf structure,
          // so pass the *parent* uiSchemaNode down for the recursive call.
          mergeUiSchemaIntoSchema(subSchema, uiSchemaNode);
        }
      }
    }

    // Handle array "items" (both simple arrays and tuples)
    if (schemaNode.has("items")) {
      JsonNode itemsSchema = schemaNode.get("items");
      JsonNode itemsUiSchema = uiSchemaNode.get("items");

      if (itemsSchema.isObject()) {
        // Case A: Simple array (items is an object)
        mergeUiSchemaIntoSchema(itemsSchema, itemsUiSchema != null ? itemsUiSchema : uiSchemaNode);
      } else if (itemsSchema.isArray()) {
        // Case B: Tuple validation (items is an array)
        if (itemsUiSchema != null && itemsUiSchema.isArray()) {
          for (int i = 0; i < itemsSchema.size(); i++) {
            if (i < itemsUiSchema.size()) { // Safely access corresponding uiSchema
              mergeUiSchemaIntoSchema(itemsSchema.get(i), itemsUiSchema.get(i));
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
          JsonNode childUiSchema = uiSchemaNode.get(fieldName);
          mergeUiSchemaIntoSchema(childSchema, childUiSchema);
        }
      }
    }
  }

}
