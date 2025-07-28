package com.github.wnameless.spring.boot.up.jsf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;

/**
 * Result of flattening a JSON Schema with conditional statements.
 * Contains the flattened schema, field origins, conditional dependencies, and JSON path mappings.
 */
@Data
public class FlattenedSchemaResult {
  
  /**
   * The flattened JSON Schema as a Map
   */
  private final Map<String, Object> flattenedSchema;
  
  /**
   * Map of field names to their origins (e.g., ORIGINAL_REQUIRED, THEN_BRANCH, etc.)
   */
  private final Map<String, FieldOrigin> fieldOrigins;
  
  /**
   * List of conditional dependencies tracking if/then/else relationships
   */
  private final List<ConditionalDependency> conditionalDependencies;
  
  /**
   * Original required fields from the base schema
   */
  private final Set<String> originalRequiredFields;
  
  /**
   * Map of flattened field names to their original JSON schema paths
   * Example: "ifConditions.constPath" -> "$.properties.ifConditions.items.properties.constPath"
   */
  private final Map<String, String> fieldJsonPaths;
  
  /**
   * Map of flattened field names to their array depth information
   * Example: "ifConditions.constPath" -> 1 (single level array)
   */
  private final Map<String, Integer> fieldArrayDepths;
  
  public FlattenedSchemaResult(Map<String, Object> flattenedSchema, 
      Map<String, FieldOrigin> fieldOrigins,
      List<ConditionalDependency> conditionalDependencies,
      Set<String> originalRequiredFields,
      Map<String, String> fieldJsonPaths,
      Map<String, Integer> fieldArrayDepths) {
    this.flattenedSchema = flattenedSchema;
    this.fieldOrigins = fieldOrigins;
    this.conditionalDependencies = conditionalDependencies;
    this.originalRequiredFields = originalRequiredFields;
    this.fieldJsonPaths = fieldJsonPaths;
    this.fieldArrayDepths = fieldArrayDepths;
  }
  
  // Backward compatibility constructor
  public FlattenedSchemaResult(Map<String, Object> flattenedSchema, 
      Map<String, FieldOrigin> fieldOrigins,
      List<ConditionalDependency> conditionalDependencies,
      Set<String> originalRequiredFields) {
    this(flattenedSchema, fieldOrigins, conditionalDependencies, originalRequiredFields, 
         new HashMap<>(), new HashMap<>());
  }
  
  /**
   * Get all dependencies for a specific field
   * @param fieldName The field to check dependencies for
   * @return List of dependencies for this field
   */
  public List<ConditionalDependency> getDependenciesForField(String fieldName) {
    List<ConditionalDependency> deps = new ArrayList<>();
    for (ConditionalDependency dep : conditionalDependencies) {
      if (dep.getDependentField().equals(fieldName)) {
        deps.add(dep);
      }
    }
    return deps;
  }
  
  /**
   * Check if a field has any conditional dependencies
   * @param fieldName The field to check
   * @return true if the field has dependencies
   */
  public boolean hasConditionalDependencies(String fieldName) {
    return conditionalDependencies.stream()
        .anyMatch(dep -> dep.getDependentField().equals(fieldName));
  }
  
  /**
   * Get all fields that depend on a specific condition field
   * @param conditionField The condition field name
   * @return Map of dependent fields to their dependencies
   */
  public Map<String, List<ConditionalDependency>> getDependentFields(String conditionField) {
    Map<String, List<ConditionalDependency>> dependentFields = new HashMap<>();
    for (ConditionalDependency dep : conditionalDependencies) {
      if (dep.getConditionField().equals(conditionField)) {
        dependentFields.computeIfAbsent(dep.getDependentField(), k -> new ArrayList<>())
            .add(dep);
      }
    }
    return dependentFields;
  }
  
  /**
   * Get the JSON path for a flattened field name
   * @param fieldName The flattened field name
   * @return The JSON schema path, or null if not found
   */
  public String getJsonPath(String fieldName) {
    return fieldJsonPaths.get(fieldName);
  }
  
  /**
   * Get the array depth for a flattened field name
   * @param fieldName The flattened field name
   * @return The array depth (0 = not array, 1 = single level array, etc.)
   */
  public int getArrayDepth(String fieldName) {
    return fieldArrayDepths.getOrDefault(fieldName, 0);
  }
  
  /**
   * Check if a field is from an array property
   * @param fieldName The flattened field name
   * @return true if the field is from an array
   */
  public boolean isArrayField(String fieldName) {
    return getArrayDepth(fieldName) > 0;
  }
  
  /**
   * Get all array fields grouped by their parent array name
   * @return Map of array parent names to their child fields
   */
  public Map<String, List<String>> getArrayFieldGroups() {
    Map<String, List<String>> groups = new HashMap<>();
    for (String fieldName : fieldJsonPaths.keySet()) {
      if (isArrayField(fieldName) && fieldName.contains(".")) {
        String arrayParent = fieldName.substring(0, fieldName.indexOf("."));
        groups.computeIfAbsent(arrayParent, k -> new ArrayList<>()).add(fieldName);
      }
    }
    return groups;
  }
}