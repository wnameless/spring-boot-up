package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * Utility class for parsing and formatting array values in Excel cells.
 * Supports comma-separated values with escape sequences and indexed notation for nested arrays.
 */
public class ArrayValueParser {
  
  private static final Pattern INDEX_PATTERN = Pattern.compile("\\[(\\d+(?:,\\d+)*)\\]");
  private static final String ESCAPE_COMMA = "\\,";
  private static final String ESCAPE_BACKSLASH = "\\\\";
  private static final String ESCAPE_BRACKET_OPEN = "\\[";
  private static final String ESCAPE_BRACKET_CLOSE = "\\]";
  private static final String TEMP_COMMA = "\u0001";
  private static final String TEMP_BACKSLASH = "\u0002";
  private static final String TEMP_BRACKET_OPEN = "\u0003";
  private static final String TEMP_BRACKET_CLOSE = "\u0004";
  
  /**
   * Parse a comma-separated string into an array, handling escape sequences.
   * 
   * @param value The comma-separated string
   * @param itemType The type of array items (string, number, boolean, etc.)
   * @return List of parsed values
   */
  public static List<Object> parseArrayValue(String value, String itemType) {
    if (value == null || value.trim().isEmpty()) {
      return new ArrayList<>();
    }
    
    // Replace escape sequences with temporary placeholders
    String processed = value
        .replace(ESCAPE_BACKSLASH, TEMP_BACKSLASH)
        .replace(ESCAPE_COMMA, TEMP_COMMA)
        .replace(ESCAPE_BRACKET_OPEN, TEMP_BRACKET_OPEN)
        .replace(ESCAPE_BRACKET_CLOSE, TEMP_BRACKET_CLOSE);
    
    // Split by comma
    String[] parts = processed.split(",", -1);
    
    List<Object> result = new ArrayList<>();
    for (String part : parts) {
      // Restore escape sequences
      String restored = part.trim()
          .replace(TEMP_COMMA, ",")
          .replace(TEMP_BACKSLASH, "\\")
          .replace(TEMP_BRACKET_OPEN, "[")
          .replace(TEMP_BRACKET_CLOSE, "]");
      
      if (!restored.isEmpty()) {
        result.add(convertValue(restored, itemType));
      }
    }
    
    return result;
  }
  
  /**
   * Parse a string with indexed notation into a nested array structure.
   * Example: "A,[0,0],B,[0,1],C,[1,0]" -> [["A","B"],["C"]]
   * 
   * @param value The string with indexed notation
   * @param itemType The type of array items
   * @return Nested array structure
   */
  public static Object parseNestedArrayValue(String value, String itemType) {
    if (value == null || value.trim().isEmpty()) {
      return new ArrayList<>();
    }
    
    // Replace escape sequences with temporary placeholders
    String processed = value
        .replace(ESCAPE_BACKSLASH, TEMP_BACKSLASH)
        .replace(ESCAPE_COMMA, TEMP_COMMA)
        .replace(ESCAPE_BRACKET_OPEN, TEMP_BRACKET_OPEN)
        .replace(ESCAPE_BRACKET_CLOSE, TEMP_BRACKET_CLOSE);
    
    List<ValueIndexPair> pairs = new ArrayList<>();
    Matcher matcher = INDEX_PATTERN.matcher(processed);
    int lastEnd = 0;
    
    while (matcher.find()) {
      // Get the value before the index
      if (matcher.start() > lastEnd) {
        String valueStr = processed.substring(lastEnd, matcher.start()).trim();
        if (valueStr.endsWith(",")) {
          valueStr = valueStr.substring(0, valueStr.length() - 1).trim();
        }
        
        // Restore escape sequences
        valueStr = valueStr
            .replace(TEMP_COMMA, ",")
            .replace(TEMP_BACKSLASH, "\\")
            .replace(TEMP_BRACKET_OPEN, "[")
            .replace(TEMP_BRACKET_CLOSE, "]");
        
        // Parse indices
        String indexStr = matcher.group(1);
        String[] indexParts = indexStr.split(",");
        int[] indices = new int[indexParts.length];
        for (int i = 0; i < indexParts.length; i++) {
          indices[i] = Integer.parseInt(indexParts[i].trim());
        }
        
        pairs.add(new ValueIndexPair(convertValue(valueStr, itemType), indices));
      }
      lastEnd = matcher.end();
      
      // Skip comma after index if present
      if (lastEnd < processed.length() && processed.charAt(lastEnd) == ',') {
        lastEnd++;
      }
    }
    
    // Build nested array structure
    return buildNestedArray(pairs);
  }
  
  /**
   * Format an array value for display in Excel cell.
   * 
   * @param array The array to format
   * @return Formatted string representation
   */
  public static String formatArrayValue(Object array) {
    if (array == null) {
      return "";
    }
    
    if (array instanceof List) {
      return formatSimpleArray((List<?>) array);
    } else if (array instanceof Object[]) {
      List<Object> list = new ArrayList<>();
      for (Object item : (Object[]) array) {
        list.add(item);
      }
      return formatSimpleArray(list);
    } else if (array instanceof JsonNode && ((JsonNode) array).isArray()) {
      return formatJsonArray((ArrayNode) array);
    }
    
    // For nested arrays, use indexed notation
    return formatNestedArray(array, new ArrayList<>());
  }
  
  private static String formatSimpleArray(List<?> array) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < array.size(); i++) {
      if (i > 0) sb.append(", ");
      String value = String.valueOf(array.get(i));
      // Apply escape sequences
      value = value
          .replace("\\", ESCAPE_BACKSLASH)
          .replace(",", ESCAPE_COMMA)
          .replace("[", ESCAPE_BRACKET_OPEN)
          .replace("]", ESCAPE_BRACKET_CLOSE);
      sb.append(value);
    }
    return sb.toString();
  }
  
  private static String formatJsonArray(ArrayNode array) {
    List<Object> list = new ArrayList<>();
    for (JsonNode node : array) {
      if (node.isTextual()) {
        list.add(node.asText());
      } else if (node.isNumber()) {
        list.add(node.numberValue());
      } else if (node.isBoolean()) {
        list.add(node.asBoolean());
      } else {
        list.add(node.toString());
      }
    }
    return formatSimpleArray(list);
  }
  
  private static String formatNestedArray(Object array, List<Integer> currentIndex) {
    StringBuilder sb = new StringBuilder();
    
    if (array instanceof List) {
      List<?> list = (List<?>) array;
      for (int i = 0; i < list.size(); i++) {
        List<Integer> newIndex = new ArrayList<>(currentIndex);
        newIndex.add(i);
        
        Object item = list.get(i);
        if (item instanceof List) {
          // Nested array
          String nested = formatNestedArray(item, newIndex);
          if (sb.length() > 0 && !nested.isEmpty()) sb.append(",");
          sb.append(nested);
        } else {
          // Leaf value
          if (sb.length() > 0) sb.append(",");
          String value = String.valueOf(item);
          // Apply escape sequences
          value = value
              .replace("\\", ESCAPE_BACKSLASH)
              .replace(",", ESCAPE_COMMA)
              .replace("[", ESCAPE_BRACKET_OPEN)
              .replace("]", ESCAPE_BRACKET_CLOSE);
          sb.append(value);
          sb.append(",[");
          for (int j = 0; j < newIndex.size(); j++) {
            if (j > 0) sb.append(",");
            sb.append(newIndex.get(j));
          }
          sb.append("]");
        }
      }
    }
    
    return sb.toString();
  }
  
  private static Object convertValue(String value, String type) {
    if (value == null || value.isEmpty()) {
      return value;
    }
    
    switch (type) {
      case "number":
      case "integer":
        try {
          if (type.equals("integer")) {
            return Integer.parseInt(value);
          } else {
            return Double.parseDouble(value);
          }
        } catch (NumberFormatException e) {
          return value;
        }
        
      case "boolean":
        return Boolean.parseBoolean(value);
        
      default:
        return value;
    }
  }
  
  private static Object buildNestedArray(List<ValueIndexPair> pairs) {
    if (pairs.isEmpty()) {
      return new ArrayList<>();
    }
    
    // Determine max dimensions
    int maxDepth = 0;
    for (ValueIndexPair pair : pairs) {
      maxDepth = Math.max(maxDepth, pair.indices.length);
    }
    
    // Build array recursively
    return buildArrayLevel(pairs, 0, new int[0]);
  }
  
  private static Object buildArrayLevel(List<ValueIndexPair> pairs, int level, int[] parentIndices) {
    List<Object> result = new ArrayList<>();
    
    // Find all unique indices at this level
    int maxIndex = -1;
    for (ValueIndexPair pair : pairs) {
      if (matchesParent(pair.indices, parentIndices) && pair.indices.length > level) {
        maxIndex = Math.max(maxIndex, pair.indices[level]);
      }
    }
    
    // Build array for this level
    for (int i = 0; i <= maxIndex; i++) {
      int[] currentIndices = new int[parentIndices.length + 1];
      System.arraycopy(parentIndices, 0, currentIndices, 0, parentIndices.length);
      currentIndices[currentIndices.length - 1] = i;
      
      // Check if this is a leaf
      boolean isLeaf = false;
      Object leafValue = null;
      for (ValueIndexPair pair : pairs) {
        if (matchesExact(pair.indices, currentIndices)) {
          isLeaf = true;
          leafValue = pair.value;
          break;
        }
      }
      
      if (isLeaf) {
        result.add(leafValue);
      } else {
        // Recurse to next level
        Object subArray = buildArrayLevel(pairs, level + 1, currentIndices);
        result.add(subArray);
      }
    }
    
    return result;
  }
  
  private static boolean matchesParent(int[] childIndices, int[] parentIndices) {
    if (childIndices.length < parentIndices.length) {
      return false;
    }
    for (int i = 0; i < parentIndices.length; i++) {
      if (childIndices[i] != parentIndices[i]) {
        return false;
      }
    }
    return true;
  }
  
  private static boolean matchesExact(int[] indices1, int[] indices2) {
    if (indices1.length != indices2.length) {
      return false;
    }
    for (int i = 0; i < indices1.length; i++) {
      if (indices1[i] != indices2[i]) {
        return false;
      }
    }
    return true;
  }
  
  private static class ValueIndexPair {
    final Object value;
    final int[] indices;
    
    ValueIndexPair(Object value, int[] indices) {
      this.value = value;
      this.indices = indices;
    }
  }
}