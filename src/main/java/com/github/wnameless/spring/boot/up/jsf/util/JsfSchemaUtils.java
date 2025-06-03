package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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

  public String trimSchemaByFields(String jsonSchema, boolean keyAsMissingTitle,
      List<JsfSimpleField> fields) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode originalRoot = mapper.readTree(jsonSchema);

      ObjectNode newRoot = mapper.createObjectNode();
      for (Iterator<String> it = originalRoot.fieldNames(); it.hasNext();) {
        String key = it.next();
        if (!key.equals("properties") && !key.equals("items") && !key.equals("required")) {
          newRoot.set(key, originalRoot.get(key));
        }
      }
      newRoot.put("type", "object");

      ObjectNode rootProperties = mapper.createObjectNode();
      Map<String, Set<String>> requiredAtPath = new HashMap<>(); // e.g., "a.b" -> {"c", ...}

      // Map of full path to the original 'required' Set at each object level
      Map<String, Set<String>> originalRequiredByPath =
          collectOriginalRequiredByPath(originalRoot, "", "");

      for (JsfSimpleField field : fields) {
        String[] pathSegments = field.getFlattenedKey().split("\\.");
        JsonNode origNode = originalRoot.get("properties");
        ObjectNode currProps = rootProperties;
        StringBuilder pathSoFar = new StringBuilder();
        List<ObjectNode> createdObjects = new ArrayList<>();

        for (int i = 0; i < pathSegments.length; i++) {
          String segment = pathSegments[i];
          if (pathSoFar.length() > 0) pathSoFar.append(".");
          pathSoFar.append(segment);
          boolean isLeaf = (i == pathSegments.length - 1);

          JsonNode origProp = origNode != null ? origNode.get(segment) : null;
          if (origProp == null) break;

          if (!isLeaf) {
            String type = getType(origProp);
            if ("object".equals(type)) {
              ObjectNode nextObj;
              if (currProps.has(segment)) {
                nextObj = (ObjectNode) currProps.get(segment);
              } else {
                nextObj = mapper.createObjectNode();
                nextObj.put("type", "object");
                nextObj.set("properties", mapper.createObjectNode());
                currProps.set(segment, nextObj);
              }
              createdObjects.add(nextObj);

              // Track 'required' fields for this path
              String currPath = pathSoFar.toString();
              if (!requiredAtPath.containsKey(currPath)) {
                requiredAtPath.put(currPath, new HashSet<>());
              }
              // Only record the next property in required if it's required in the original schema
              // at this path
              Set<String> origRequired = originalRequiredByPath
                  .getOrDefault(parentPath(pathSoFar.toString()), Collections.emptySet());
              if (origRequired.contains(segment)) {
                requiredAtPath.get(currPath).add(segment);
              }

              origNode = origProp.get("properties");
              currProps = (ObjectNode) nextObj.get("properties");
            } else if ("array".equals(type)) {
              ObjectNode nextArr;
              if (currProps.has(segment)) {
                nextArr = (ObjectNode) currProps.get(segment);
              } else {
                nextArr = mapper.createObjectNode();
                nextArr.put("type", "array");
                ObjectNode itemsNode = mapper.createObjectNode();
                nextArr.set("items", itemsNode);
                currProps.set(segment, nextArr);
              }
              origNode = origProp.get("items");
              currProps = (ObjectNode) nextArr.get("items");
            } else {
              break;
            }
          } else {
            // Leaf property
            currProps.set(segment, origProp.deepCopy());
            // If this leaf is required, mark for parent
            String currPath = parentPath(pathSoFar.toString());
            if (!requiredAtPath.containsKey(currPath)) {
              requiredAtPath.put(currPath, new HashSet<>());
            }
            Set<String> origRequired =
                originalRequiredByPath.getOrDefault(currPath, Collections.emptySet());
            if (origRequired.contains(segment)) {
              requiredAtPath.get(currPath).add(segment);
            }
          }
        }
      }

      // Recursively set required arrays in the result schema tree
      setRequiredRecursively(newRoot, "", requiredAtPath);

      newRoot.set("properties", rootProperties);

      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(newRoot);
    } catch (Exception e) {
      throw new RuntimeException("Failed to trim schema", e);
    }
  }

  // Recursively walk the schema and add required fields at each object level
  private void setRequiredRecursively(ObjectNode node, String path,
      Map<String, Set<String>> requiredAtPath) {
    if (!"object".equals(getType(node))) return;
    ObjectNode props = (ObjectNode) node.get("properties");
    if (props == null) return;

    Set<String> requiredFields = requiredAtPath.get(path);
    if (requiredFields != null && !requiredFields.isEmpty()) {
      ArrayNode req = node.arrayNode();
      for (String f : requiredFields)
        req.add(f);
      node.set("required", req);
    }
    // For children
    for (Iterator<String> it = props.fieldNames(); it.hasNext();) {
      String prop = it.next();
      JsonNode child = props.get(prop);
      String nextPath = path.isEmpty() ? prop : path + "." + prop;
      if (child.has("type") && "object".equals(getType(child))) {
        setRequiredRecursively((ObjectNode) child, nextPath, requiredAtPath);
      } else if (child.has("type") && "array".equals(getType(child))) {
        JsonNode items = child.get("items");
        if (items != null && items.isObject()) {
          setRequiredRecursively((ObjectNode) items, nextPath, requiredAtPath);
        }
      }
    }
  }

  // Collect the original required set for every object path in the schema
  private Map<String, Set<String>> collectOriginalRequiredByPath(JsonNode node, String path,
      String propName) {
    Map<String, Set<String>> map = new HashMap<>();
    if ("object".equals(getType(node))) {
      String currPath =
          path.isEmpty() ? propName : path + (propName.isEmpty() ? "" : "." + propName);
      if (node.has("required")) {
        Set<String> req = new HashSet<>();
        node.get("required").forEach(x -> req.add(x.asText()));
        map.put(currPath, req);
      }
      JsonNode props = node.get("properties");
      if (props != null && props.isObject()) {
        for (Iterator<String> it = props.fieldNames(); it.hasNext();) {
          String childProp = it.next();
          JsonNode childNode = props.get(childProp);
          map.putAll(collectOriginalRequiredByPath(childNode,
              currPath.isEmpty() ? childProp : currPath, ""));
        }
      }
    } else if ("array".equals(getType(node))) {
      JsonNode items = node.get("items");
      if (items != null && items.isObject()) {
        map.putAll(collectOriginalRequiredByPath(items, path, propName));
      }
    }
    return map;
  }

  private String parentPath(String path) {
    int idx = path.lastIndexOf('.');
    return (idx == -1) ? "" : path.substring(0, idx);
  }

}
