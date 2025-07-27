package com.github.wnameless.spring.boot.up.jsf.model;

import java.util.Map;
import lombok.Data;

/**
 * Represents a conditional dependency in JSON Schema (if/then/else).
 * Tracks which fields depend on specific conditions.
 */
@Data
public class ConditionalDependency {
  
  /**
   * The field name that triggers the condition
   */
  private final String conditionField;
  
  /**
   * The expected value for the condition to be true
   */
  private final Object conditionValue;
  
  /**
   * The comparison operator (e.g., "equals", "const", "enum")
   */
  private final String operator;
  
  /**
   * The dependent field name
   */
  private final String dependentField;
  
  /**
   * Whether this dependency is from a "then" branch (true) or "else" branch (false)
   */
  private final boolean isThenBranch;
  
  /**
   * Additional condition properties if the condition is complex
   */
  private final Map<String, Object> complexCondition;
  
  public ConditionalDependency(String conditionField, Object conditionValue, String operator,
      String dependentField, boolean isThenBranch) {
    this(conditionField, conditionValue, operator, dependentField, isThenBranch, null);
  }
  
  public ConditionalDependency(String conditionField, Object conditionValue, String operator,
      String dependentField, boolean isThenBranch, Map<String, Object> complexCondition) {
    this.conditionField = conditionField;
    this.conditionValue = conditionValue;
    this.operator = operator;
    this.dependentField = dependentField;
    this.isThenBranch = isThenBranch;
    this.complexCondition = complexCondition;
  }
  
  /**
   * Generates an Excel formula to check if this dependency is active
   * @param conditionFieldColumn The column letter where the condition field value is located
   * @param currentRow The current row number
   * @return Excel formula string
   */
  public String toExcelFormula(String conditionFieldColumn, int currentRow) {
    if ("const".equals(operator) || "equals".equals(operator)) {
      return String.format("IF($%s%d=\"%s\",\"ACTIVE\",\"INACTIVE\")", 
          conditionFieldColumn, currentRow, conditionValue);
    } else if ("enum".equals(operator) && conditionValue instanceof String) {
      // For enum, check if the value matches any in the list
      return String.format("IF($%s%d=\"%s\",\"ACTIVE\",\"INACTIVE\")", 
          conditionFieldColumn, currentRow, conditionValue);
    }
    // Default to inactive if operator is not recognized
    return "\"INACTIVE\"";
  }
}