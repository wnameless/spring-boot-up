package com.github.wnameless.spring.boot.up.jsf.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Data;

/**
 * Result of flattening a JSON Schema with conditional statements.
 * Contains the flattened schema, field origins, and conditional dependencies.
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
  
  public FlattenedSchemaResult(Map<String, Object> flattenedSchema, 
      Map<String, FieldOrigin> fieldOrigins,
      List<ConditionalDependency> conditionalDependencies,
      Set<String> originalRequiredFields) {
    this.flattenedSchema = flattenedSchema;
    this.fieldOrigins = fieldOrigins;
    this.conditionalDependencies = conditionalDependencies;
    this.originalRequiredFields = originalRequiredFields;
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
}