package com.github.wnameless.spring.boot.up.jsf.model;

/**
 * Enum to track the origin of fields in a flattened JSON Schema.
 * Used for applying conditional formatting in Excel workbooks.
 */
public enum FieldOrigin {
  /**
   * Field from original schema marked as required
   */
  ORIGINAL_REQUIRED,
  
  /**
   * Field from original schema marked as optional
   */
  ORIGINAL_OPTIONAL,
  
  /**
   * Field merged from allOf statement
   */
  ALL_OF,
  
  /**
   * Field merged from anyOf statement
   */
  ANY_OF,
  
  /**
   * Field merged from oneOf statement
   */
  ONE_OF,
  
  /**
   * Field from if condition properties
   */
  IF_CONDITION,
  
  /**
   * Field merged from then branch
   */
  THEN_BRANCH,
  
  /**
   * Field merged from else branch
   */
  ELSE_BRANCH
}