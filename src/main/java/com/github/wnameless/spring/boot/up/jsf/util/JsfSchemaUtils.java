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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.wnameless.spring.boot.up.jsf.model.ConditionalDependency;
import com.github.wnameless.spring.boot.up.jsf.model.FieldOrigin;
import com.github.wnameless.spring.boot.up.jsf.model.FlattenedSchemaResult;
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
    return schemaToFields(jsonSchema, keyAsMissingTitle, false);
  }

  public List<JsfSimpleField> schemaToFields(String jsonSchema, boolean keyAsMissingTitle,
      boolean doNotTraverseNestedArray) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(jsonSchema);
      List<JsfSimpleField> fields = new ArrayList<>();
      traverseSchema(root, "", "$", false, new ArrayList<>(), "", fields, keyAsMissingTitle,
          doNotTraverseNestedArray);
      return fields;
    } catch (Exception e) {
      throw new RuntimeException("Failed to parse schema", e);
    }
  }

  private void traverseSchema(JsonNode schemaNode, String flattenedPrefix, String jsonPathPrefix,
      boolean parentIsArray, List<String> parentTitles, String currentPropName,
      List<JsfSimpleField> fields, boolean keyAsMissingTitle, boolean doNotTraverseNestedArray) {
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
              keyAsMissingTitle, doNotTraverseNestedArray);
        }
      }
    } else if ("array".equals(type)) {
      if (doNotTraverseNestedArray) {
        // When doNotTraverseNestedArray is true, add the array itself as a field
        fields.add(new JsfSimpleField(currentTitles, flattenedPrefix, jsonPathPrefix, parentIsArray,
            hasTitle));
      } else {
        // Original behavior: traverse into array items
        JsonNode items = schemaNode.get("items");
        String nextFlat = flattenedPrefix;
        String nextJsonPath = jsonPathPrefix + ".items";
        // For arrays, currentPropName not updated
        traverseSchema(items, nextFlat, nextJsonPath, true, currentTitles, currentPropName, fields,
            keyAsMissingTitle, doNotTraverseNestedArray);
      }
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

  public Map<String, Object> flattenConditionalSchema(Map<String, Object> jsonSchema) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writeValueAsString(jsonSchema);
      String flattened = flattenConditionalSchema(json);
      return mapper.readValue(flattened, new TypeReference<Map<String, Object>>() {});
    } catch (Exception e) {
      throw new RuntimeException("Failed to process schema", e);
    }
  }

  public String flattenConditionalSchema(String jsonSchema) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(jsonSchema);
      JsonNode flattened = flattenConditionalNode(root, mapper);
      return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(flattened);
    } catch (Exception e) {
      throw new RuntimeException("Failed to flatten conditional schema", e);
    }
  }

  private JsonNode flattenConditionalNode(JsonNode node, ObjectMapper mapper) {
    if (!node.isObject()) {
      return node.deepCopy();
    }

    ObjectNode result = mapper.createObjectNode();

    // Copy all non-conditional fields
    for (Iterator<String> it = node.fieldNames(); it.hasNext();) {
      String fieldName = it.next();
      if (!isConditionalKeyword(fieldName)) {
        JsonNode fieldValue = node.get(fieldName);
        if ("properties".equals(fieldName) && fieldValue.isObject()) {
          // Recursively flatten properties
          ObjectNode flattenedProps = mapper.createObjectNode();
          for (Iterator<String> propIt = fieldValue.fieldNames(); propIt.hasNext();) {
            String propName = propIt.next();
            flattenedProps.set(propName, flattenConditionalNode(fieldValue.get(propName), mapper));
          }
          result.set(fieldName, flattenedProps);
        } else if ("items".equals(fieldName) && fieldValue.isObject()) {
          // Recursively flatten items
          result.set(fieldName, flattenConditionalNode(fieldValue, mapper));
        } else {
          result.set(fieldName, fieldValue.deepCopy());
        }
      }
    }

    // Merge properties from conditional keywords
    ObjectNode mergedProperties = extractAndMergeConditionalProperties(node, mapper);
    if (mergedProperties.size() > 0) {
      ObjectNode existingProps = (ObjectNode) result.get("properties");
      if (existingProps == null) {
        existingProps = mapper.createObjectNode();
        result.set("properties", existingProps);
      }
      // Merge conditional properties into existing properties
      for (Iterator<String> it = mergedProperties.fieldNames(); it.hasNext();) {
        String propName = it.next();
        if (!existingProps.has(propName)) {
          existingProps.set(propName, mergedProperties.get(propName));
        }
      }
    }

    return result;
  }

  private boolean isConditionalKeyword(String keyword) {
    return "allOf".equals(keyword) || "anyOf".equals(keyword) || "oneOf".equals(keyword)
        || "if".equals(keyword) || "then".equals(keyword) || "else".equals(keyword);
  }

  private ObjectNode extractAndMergeConditionalProperties(JsonNode node, ObjectMapper mapper) {
    ObjectNode mergedProps = mapper.createObjectNode();

    // Process allOf
    if (node.has("allOf") && node.get("allOf").isArray()) {
      for (JsonNode subSchema : node.get("allOf")) {
        mergeSchemaProperties(mergedProps, flattenConditionalNode(subSchema, mapper), mapper);
      }
    }

    // Process anyOf
    if (node.has("anyOf") && node.get("anyOf").isArray()) {
      for (JsonNode subSchema : node.get("anyOf")) {
        mergeSchemaProperties(mergedProps, flattenConditionalNode(subSchema, mapper), mapper);
      }
    }

    // Process oneOf
    if (node.has("oneOf") && node.get("oneOf").isArray()) {
      for (JsonNode subSchema : node.get("oneOf")) {
        mergeSchemaProperties(mergedProps, flattenConditionalNode(subSchema, mapper), mapper);
      }
    }

    // Process if/then/else
    if (node.has("if")) {
      if (node.has("then")) {
        mergeSchemaProperties(mergedProps, flattenConditionalNode(node.get("then"), mapper),
            mapper);
      }
      if (node.has("else")) {
        mergeSchemaProperties(mergedProps, flattenConditionalNode(node.get("else"), mapper),
            mapper);
      }
    }

    return mergedProps;
  }

  private void mergeSchemaProperties(ObjectNode target, JsonNode source, ObjectMapper mapper) {
    if (!source.isObject()) return;

    // Merge properties
    if (source.has("properties") && source.get("properties").isObject()) {
      JsonNode sourceProps = source.get("properties");
      for (Iterator<String> it = sourceProps.fieldNames(); it.hasNext();) {
        String propName = it.next();
        if (!target.has(propName)) {
          target.set(propName, sourceProps.get(propName).deepCopy());
        }
      }
    }

    // Also merge any nested conditional properties from the source
    ObjectNode nestedConditionals = extractAndMergeConditionalProperties(source, mapper);
    for (Iterator<String> it = nestedConditionals.fieldNames(); it.hasNext();) {
      String propName = it.next();
      if (!target.has(propName)) {
        target.set(propName, nestedConditionals.get(propName));
      }
    }
  }

  public FlattenedSchemaResult flattenConditionalSchemaWithOrigins(String jsonSchema) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode root = mapper.readTree(jsonSchema);

      Map<String, FieldOrigin> fieldOrigins = new HashMap<>();
      List<ConditionalDependency> dependencies = new ArrayList<>();
      Set<String> originalRequired = new HashSet<>();
      Map<String, String> fieldJsonPaths = new HashMap<>();
      Map<String, Integer> fieldArrayDepths = new HashMap<>();

      // Collect original required fields
      if (root.has("required") && root.get("required").isArray()) {
        root.get("required").forEach(req -> originalRequired.add(req.asText()));
      }

      // Track origins and JSON paths while flattening
      JsonNode flattened = flattenConditionalNodeWithOrigins(root, mapper, fieldOrigins,
          dependencies, "$.properties", originalRequired, fieldJsonPaths, fieldArrayDepths);

      // Convert to Map
      Map<String, Object> flattenedMap = mapper.readValue(mapper.writeValueAsString(flattened),
          new TypeReference<Map<String, Object>>() {});

      return new FlattenedSchemaResult(flattenedMap, fieldOrigins, dependencies, originalRequired,
          fieldJsonPaths, fieldArrayDepths);
    } catch (Exception e) {
      throw new RuntimeException("Failed to flatten conditional schema with origins", e);
    }
  }

  public FlattenedSchemaResult flattenConditionalSchemaWithOrigins(Map<String, Object> jsonSchema) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      String json = mapper.writeValueAsString(jsonSchema);
      return flattenConditionalSchemaWithOrigins(json);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to process schema", e);
    }
  }

  private JsonNode flattenConditionalNodeWithOrigins(JsonNode node, ObjectMapper mapper,
      Map<String, FieldOrigin> fieldOrigins, List<ConditionalDependency> dependencies,
      String currentPath, Set<String> originalRequired, Map<String, String> fieldJsonPaths,
      Map<String, Integer> fieldArrayDepths) {
    if (!node.isObject()) {
      return node.deepCopy();
    }

    ObjectNode result = mapper.createObjectNode();

    // Copy all non-conditional fields
    for (Iterator<String> it = node.fieldNames(); it.hasNext();) {
      String fieldName = it.next();
      if (!isConditionalKeyword(fieldName)) {
        JsonNode fieldValue = node.get(fieldName);
        if ("properties".equals(fieldName) && fieldValue.isObject()) {
          // Process original properties
          ObjectNode flattenedProps = mapper.createObjectNode();
          for (Iterator<String> propIt = fieldValue.fieldNames(); propIt.hasNext();) {
            String propName = propIt.next();
            JsonNode propSchema = fieldValue.get(propName);
            // Track origin as ORIGINAL_REQUIRED or ORIGINAL_OPTIONAL
            FieldOrigin origin = originalRequired.contains(propName) ? FieldOrigin.ORIGINAL_REQUIRED
                : FieldOrigin.ORIGINAL_OPTIONAL;
            
            // Check if this is an array with object items
            if (isArrayOfObjects(propSchema)) {
              // Flatten array[object] properties
              String propJsonPath = currentPath + "." + propName;
              flattenArrayProperty(flattenedProps, propName, propSchema, origin, fieldOrigins, 
                  mapper, propJsonPath, fieldJsonPaths, fieldArrayDepths);
            } else {
              // Regular property
              fieldOrigins.put(propName, origin);
              flattenedProps.set(propName, propSchema.deepCopy());
              // Track JSON path for regular properties
              fieldJsonPaths.put(propName, currentPath + "." + propName);
              fieldArrayDepths.put(propName, 0);
            }
          }
          result.set(fieldName, flattenedProps);
        } else {
          result.set(fieldName, fieldValue.deepCopy());
        }
      }
    }

    // Process conditional keywords with origin tracking
    processConditionalsWithOrigins(node, mapper, result, fieldOrigins, dependencies, currentPath,
        fieldJsonPaths, fieldArrayDepths);

    return result;
  }

  private void processConditionalsWithOrigins(JsonNode node, ObjectMapper mapper, ObjectNode result,
      Map<String, FieldOrigin> fieldOrigins, List<ConditionalDependency> dependencies,
      String currentPath, Map<String, String> fieldJsonPaths, Map<String, Integer> fieldArrayDepths) {

    ObjectNode existingProps = (ObjectNode) result.get("properties");
    if (existingProps == null) {
      existingProps = mapper.createObjectNode();
      result.set("properties", existingProps);
    }

    // Process allOf - recursively process each subschema
    if (node.has("allOf") && node.get("allOf").isArray()) {
      for (JsonNode subSchema : node.get("allOf")) {
        // First merge direct properties
        mergePropertiesWithOrigin(existingProps, subSchema, FieldOrigin.ALL_OF, fieldOrigins,
            mapper, currentPath, fieldJsonPaths, fieldArrayDepths);
        // Then recursively process any conditionals within this subschema
        processConditionalsWithOrigins(subSchema, mapper, result, fieldOrigins, dependencies,
            currentPath, fieldJsonPaths, fieldArrayDepths);
      }
    }

    // Process anyOf - recursively process each subschema
    if (node.has("anyOf") && node.get("anyOf").isArray()) {
      for (JsonNode subSchema : node.get("anyOf")) {
        // First merge direct properties
        mergePropertiesWithOrigin(existingProps, subSchema, FieldOrigin.ANY_OF, fieldOrigins,
            mapper, currentPath, fieldJsonPaths, fieldArrayDepths);
        // Then recursively process any conditionals within this subschema
        processConditionalsWithOrigins(subSchema, mapper, result, fieldOrigins, dependencies,
            currentPath, fieldJsonPaths, fieldArrayDepths);
      }
    }

    // Process oneOf - recursively process each subschema
    if (node.has("oneOf") && node.get("oneOf").isArray()) {
      for (JsonNode subSchema : node.get("oneOf")) {
        // First merge direct properties
        mergePropertiesWithOrigin(existingProps, subSchema, FieldOrigin.ONE_OF, fieldOrigins,
            mapper, currentPath, fieldJsonPaths, fieldArrayDepths);
        // Then recursively process any conditionals within this subschema
        processConditionalsWithOrigins(subSchema, mapper, result, fieldOrigins, dependencies,
            currentPath, fieldJsonPaths, fieldArrayDepths);
      }
    }

    // Process if/then/else with dependency tracking
    if (node.has("if")) {
      JsonNode ifNode = node.get("if");

      // Extract condition details
      String conditionField = null;
      Object conditionValue = null;
      String operator = "equals";

      if (ifNode.has("properties")) {
        JsonNode ifProps = ifNode.get("properties");
        for (Iterator<String> it = ifProps.fieldNames(); it.hasNext();) {
          conditionField = it.next();
          JsonNode condProp = ifProps.get(conditionField);
          if (condProp.has("const")) {
            conditionValue = condProp.get("const").asText();
            operator = "const";
          } else if (condProp.has("enum") && condProp.get("enum").size() == 1) {
            conditionValue = condProp.get("enum").get(0).asText();
            operator = "enum";
          }
          break; // Handle first condition only for now
        }
      }

      // Process then branch
      if (node.has("then") && conditionField != null) {
        JsonNode thenNode = node.get("then");
        if (thenNode.has("properties")) {
          JsonNode thenProps = thenNode.get("properties");
          for (Iterator<String> it = thenProps.fieldNames(); it.hasNext();) {
            String propName = it.next();
            if (!existingProps.has(propName)) {
              JsonNode propSchema = thenProps.get(propName);
              
              // Check if this is an array with object items
              if (isArrayOfObjects(propSchema)) {
                // Flatten array[object] properties
                flattenArrayProperty(existingProps, propName, propSchema, FieldOrigin.THEN_BRANCH, 
                    fieldOrigins, mapper, currentPath + "." + propName, fieldJsonPaths, fieldArrayDepths);
                // Add dependencies for each flattened property
                JsonNode itemProps = propSchema.get("items").get("properties");
                for (Iterator<String> itemIt = itemProps.fieldNames(); itemIt.hasNext();) {
                  String itemPropName = itemIt.next();
                  String flattenedName = propName + "." + itemPropName;
                  dependencies.add(new ConditionalDependency(conditionField, conditionValue, operator,
                      flattenedName, true));
                }
              } else {
                existingProps.set(propName, propSchema.deepCopy());
                fieldOrigins.put(propName, FieldOrigin.THEN_BRANCH);
                // Add dependency
                dependencies.add(new ConditionalDependency(conditionField, conditionValue, operator,
                    propName, true));
              }
            }
          }
        }
      }

      // Process else branch
      if (node.has("else") && conditionField != null) {
        JsonNode elseNode = node.get("else");
        if (elseNode.has("properties")) {
          JsonNode elseProps = elseNode.get("properties");
          for (Iterator<String> it = elseProps.fieldNames(); it.hasNext();) {
            String propName = it.next();
            if (!existingProps.has(propName)) {
              JsonNode propSchema = elseProps.get(propName);
              
              // Check if this is an array with object items
              if (isArrayOfObjects(propSchema)) {
                // Flatten array[object] properties
                flattenArrayProperty(existingProps, propName, propSchema, FieldOrigin.ELSE_BRANCH,
                    fieldOrigins, mapper, currentPath + "." + propName, fieldJsonPaths, fieldArrayDepths);
                // Add dependencies for each flattened property
                JsonNode itemProps = propSchema.get("items").get("properties");
                for (Iterator<String> itemIt = itemProps.fieldNames(); itemIt.hasNext();) {
                  String itemPropName = itemIt.next();
                  String flattenedName = propName + "." + itemPropName;
                  dependencies.add(new ConditionalDependency(conditionField, conditionValue, operator,
                      flattenedName, false));
                }
              } else {
                existingProps.set(propName, propSchema.deepCopy());
                fieldOrigins.put(propName, FieldOrigin.ELSE_BRANCH);
                // Add dependency
                dependencies.add(new ConditionalDependency(conditionField, conditionValue, operator,
                    propName, false));
              }
            }
          }
        }
      }
    }
  }

  private void mergePropertiesWithOrigin(ObjectNode target, JsonNode source, FieldOrigin origin,
      Map<String, FieldOrigin> fieldOrigins, ObjectMapper mapper, String basePath,
      Map<String, String> fieldJsonPaths, Map<String, Integer> fieldArrayDepths) {
    if (!source.isObject()) return;

    if (source.has("properties") && source.get("properties").isObject()) {
      JsonNode sourceProps = source.get("properties");
      for (Iterator<String> it = sourceProps.fieldNames(); it.hasNext();) {
        String propName = it.next();
        if (!target.has(propName)) {
          JsonNode propSchema = sourceProps.get(propName);
          
          // Check if this is an array with object items
          if (isArrayOfObjects(propSchema)) {
            // Flatten array[object] properties
            flattenArrayProperty(target, propName, propSchema, origin, fieldOrigins, mapper,
                basePath + "." + propName, fieldJsonPaths, fieldArrayDepths);
          } else {
            // Regular property
            target.set(propName, propSchema.deepCopy());
            fieldOrigins.putIfAbsent(propName, origin);
          }
        }
      }
    }
  }
  
  private boolean isArrayOfObjects(JsonNode propSchema) {
    if (!propSchema.has("type") || !"array".equals(propSchema.get("type").asText())) {
      return false;
    }
    
    JsonNode items = propSchema.get("items");
    return items != null && items.has("type") && "object".equals(items.get("type").asText());
  }
  
  private void flattenArrayProperty(ObjectNode target, String arrayPropName, JsonNode arraySchema,
      FieldOrigin origin, Map<String, FieldOrigin> fieldOrigins, ObjectMapper mapper,
      String baseJsonPath, Map<String, String> fieldJsonPaths, Map<String, Integer> fieldArrayDepths) {
    JsonNode items = arraySchema.get("items");
    if (items == null) return;
    
    // Create a modified array schema that indicates it's been flattened
    ObjectNode flattenedArraySchema = mapper.createObjectNode();
    flattenedArraySchema.put("type", "array");
    flattenedArraySchema.put("flattenedArray", true);
    
    // Create array indicator for the main property
    flattenedArraySchema.set("originalSchema", arraySchema.deepCopy());
    
    // Process regular properties in array items
    if (items.has("properties")) {
      JsonNode itemProperties = items.get("properties");
      for (Iterator<String> it = itemProperties.fieldNames(); it.hasNext();) {
        String itemPropName = it.next();
        String flattenedName = arrayPropName + "." + itemPropName;
        
        ObjectNode flattenedProp = mapper.createObjectNode();
        flattenedProp.setAll((ObjectNode) itemProperties.get(itemPropName).deepCopy());
        flattenedProp.put("arrayProperty", true);
        flattenedProp.put("arrayParent", arrayPropName);
        flattenedProp.put("arrayItemProperty", itemPropName);
        
        target.set(flattenedName, flattenedProp);
        fieldOrigins.putIfAbsent(flattenedName, origin);
        
        // Track JSON path and array depth for flattened properties
        String itemJsonPath = baseJsonPath + ".items.properties." + itemPropName;
        fieldJsonPaths.put(flattenedName, itemJsonPath);
        fieldArrayDepths.put(flattenedName, 1); // Single level array
      }
    }
    
    // Process conditional keywords in array items (anyOf, oneOf, allOf)
    processArrayItemConditionals(target, arrayPropName, items, origin, fieldOrigins, mapper,
        baseJsonPath, fieldJsonPaths, fieldArrayDepths);
    
    // Also store the array schema itself for reference
    target.set(arrayPropName, flattenedArraySchema);
    fieldOrigins.putIfAbsent(arrayPropName, origin);
    
    // Track JSON path for the array itself
    fieldJsonPaths.put(arrayPropName, baseJsonPath);
    fieldArrayDepths.put(arrayPropName, 0); // Array container, not individual elements
  }
  
  /**
   * Process conditional keywords (anyOf, oneOf, allOf) within array items
   */
  private void processArrayItemConditionals(ObjectNode target, String arrayPropName, JsonNode items,
      FieldOrigin origin, Map<String, FieldOrigin> fieldOrigins, ObjectMapper mapper,
      String baseJsonPath, Map<String, String> fieldJsonPaths, Map<String, Integer> fieldArrayDepths) {
    
    // Process anyOf in array items
    if (items.has("anyOf") && items.get("anyOf").isArray()) {
      for (JsonNode anyOfItem : items.get("anyOf")) {
        if (anyOfItem.has("properties")) {
          JsonNode anyOfProps = anyOfItem.get("properties");
          for (Iterator<String> it = anyOfProps.fieldNames(); it.hasNext();) {
            String propName = it.next();
            String flattenedName = arrayPropName + "." + propName;
            
            // Only add if not already present
            if (!target.has(flattenedName)) {
              ObjectNode flattenedProp = mapper.createObjectNode();
              flattenedProp.setAll((ObjectNode) anyOfProps.get(propName).deepCopy());
              flattenedProp.put("arrayProperty", true);
              flattenedProp.put("arrayParent", arrayPropName);
              flattenedProp.put("arrayItemProperty", propName);
              
              target.set(flattenedName, flattenedProp);
              fieldOrigins.putIfAbsent(flattenedName, FieldOrigin.ANY_OF);
              
              // Track JSON path for anyOf properties
              String itemJsonPath = baseJsonPath + ".items.anyOf.properties." + propName;
              fieldJsonPaths.put(flattenedName, itemJsonPath);
              fieldArrayDepths.put(flattenedName, 1);
            }
          }
        }
      }
    }
    
    // Process oneOf in array items
    if (items.has("oneOf") && items.get("oneOf").isArray()) {
      for (JsonNode oneOfItem : items.get("oneOf")) {
        if (oneOfItem.has("properties")) {
          JsonNode oneOfProps = oneOfItem.get("properties");
          for (Iterator<String> it = oneOfProps.fieldNames(); it.hasNext();) {
            String propName = it.next();
            String flattenedName = arrayPropName + "." + propName;
            
            if (!target.has(flattenedName)) {
              ObjectNode flattenedProp = mapper.createObjectNode();
              flattenedProp.setAll((ObjectNode) oneOfProps.get(propName).deepCopy());
              flattenedProp.put("arrayProperty", true);
              flattenedProp.put("arrayParent", arrayPropName);
              flattenedProp.put("arrayItemProperty", propName);
              
              target.set(flattenedName, flattenedProp);
              fieldOrigins.putIfAbsent(flattenedName, FieldOrigin.ONE_OF);
              
              String itemJsonPath = baseJsonPath + ".items.oneOf.properties." + propName;
              fieldJsonPaths.put(flattenedName, itemJsonPath);
              fieldArrayDepths.put(flattenedName, 1);
            }
          }
        }
      }
    }
    
    // Process allOf in array items
    if (items.has("allOf") && items.get("allOf").isArray()) {
      for (JsonNode allOfItem : items.get("allOf")) {
        if (allOfItem.has("properties")) {
          JsonNode allOfProps = allOfItem.get("properties");
          for (Iterator<String> it = allOfProps.fieldNames(); it.hasNext();) {
            String propName = it.next();
            String flattenedName = arrayPropName + "." + propName;
            
            if (!target.has(flattenedName)) {
              ObjectNode flattenedProp = mapper.createObjectNode();
              flattenedProp.setAll((ObjectNode) allOfProps.get(propName).deepCopy());
              flattenedProp.put("arrayProperty", true);
              flattenedProp.put("arrayParent", arrayPropName);
              flattenedProp.put("arrayItemProperty", propName);
              
              target.set(flattenedName, flattenedProp);
              fieldOrigins.putIfAbsent(flattenedName, FieldOrigin.ALL_OF);
              
              String itemJsonPath = baseJsonPath + ".items.allOf.properties." + propName;
              fieldJsonPaths.put(flattenedName, itemJsonPath);
              fieldArrayDepths.put(flattenedName, 1);
            }
          }
        }
      }
    }
  }

}
