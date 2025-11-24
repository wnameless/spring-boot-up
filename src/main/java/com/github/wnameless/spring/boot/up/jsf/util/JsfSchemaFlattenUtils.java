package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.LinkedHashMap;
import java.util.Map;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.github.wnameless.json.unflattener.JsonUnflattener;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsfSchemaFlattenUtils {

  public String flattenConditionalSchema(String jsonSchema) {
    Map<String, Object> falttenedJsonMap = JsonFlattener.flattenAsMap(jsonSchema);
    Map<String, Object> processedFalttenedJsonMap = new LinkedHashMap<>();

    // Process each entry in the flattened map
    for (Map.Entry<String, Object> entry : falttenedJsonMap.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      // Check if this key is part of an if-then-else conditional pattern
      if (isPartOfConditionalPattern(key)) {
        // Skip keys that are conditional keywords themselves or within conditional branches
        if (shouldSkipKey(key)) {
          continue;
        }

        // Process the key to remove conditional keywords and adjust paths
        String processedKey = processConditionalKey(key);

        // Only add if the processed key is valid (not empty after removing conditionals)
        if (processedKey != null && !processedKey.isEmpty()) {
          // Check if this key already exists (might happen when merging conditionals)
          // If it doesn't exist or the current value is not null, add/update it
          if (!processedFalttenedJsonMap.containsKey(processedKey) || value != null) {
            processedFalttenedJsonMap.put(processedKey, value);
          }
        }
      } else {
        // For non-conditional content (like oneOf with const/title), preserve as-is
        processedFalttenedJsonMap.put(key, value);
      }
    }

    return JsonUnflattener.unflatten(processedFalttenedJsonMap);
  }

  private boolean isPartOfConditionalPattern(String key) {
    // Check if this key path contains if-then-else conditional keywords
    // Only paths that actually have if/then/else should be considered conditional
    String[] segments = key.split("\\.");

    for (String segment : segments) {
      String cleanSegment = segment.replaceAll("\\[\\d+\\]", "");
      if ("if".equals(cleanSegment) || "then".equals(cleanSegment) || "else".equals(cleanSegment)) {
        return true;
      }
    }

    return false;
  }

  private boolean shouldSkipKey(String key) {
    // Skip any key that contains "if" in its path (but not "then" or "else")
    // These are condition check properties that should be completely ignored
    String[] segments = key.split("\\.");
    for (String segment : segments) {
      // Remove array indices to check the base segment
      String cleanSegment = segment.replaceAll("\\[\\d+\\]", "");
      if ("if".equals(cleanSegment)) {
        return true;
      }
    }
    return false;
  }

  private String processConditionalKey(String key) {
    // Split the key into segments
    String[] segments = key.split("\\.");
    StringBuilder result = new StringBuilder();
    boolean inConditionalAllOf = false;
    int conditionalDepth = 0;

    for (int i = 0; i < segments.length; i++) {
      String segment = segments[i];

      // Check if this segment is an array index (e.g., [0], [1])
      boolean isArrayIndex = segment.startsWith("[") && segment.endsWith("]");

      // Handle structural conditional keywords (allOf, anyOf, oneOf)
      if (isStructuralConditionalKeyword(segment)) {
        // Check if this specific structural array is part of if-then-else pattern
        // by looking ahead for if/then/else in the immediate children
        boolean isConditionalArray = false;
        for (int j = i + 1; j < segments.length && j < i + 3; j++) {
          String nextSegment = segments[j].replaceAll("\\[\\d+\\]", "");
          if ("if".equals(nextSegment) || "then".equals(nextSegment)
              || "else".equals(nextSegment)) {
            isConditionalArray = true;
            break;
          }
        }

        if (isConditionalArray) {
          inConditionalAllOf = true;
          conditionalDepth++;
          continue;
        } else {
          // For non-conditional arrays (like oneOf with const/title), keep them
          if (result.length() > 0 && !isArrayIndex) {
            result.append(".");
          }
          result.append(segment);
          continue;
        }
      }

      // Only skip array indices if we're in a conditional allOf/anyOf/oneOf
      if (inConditionalAllOf && isArrayIndex) {
        continue;
      }

      // Handle "properties" keyword
      if ("properties".equals(segment)) {
        // Skip "properties" that comes right after conditional array index
        if (inConditionalAllOf && conditionalDepth > 0) {
          conditionalDepth--;
          if (conditionalDepth == 0) {
            inConditionalAllOf = false;
          }
          continue;
        }
      }

      // Handle then/else branches - skip the keyword itself but reset conditional tracking
      if ("then".equals(segment) || "else".equals(segment)) {
        // After then/else, we're no longer in the conditional structure
        inConditionalAllOf = false;
        conditionalDepth = 0;
        continue;
      }

      // Add the segment to result
      if (result.length() > 0 && !isArrayIndex) {
        result.append(".");
      }
      result.append(segment);
    }

    return result.toString();
  }

  private boolean isStructuralConditionalKeyword(String segment) {
    // Remove array indices if present
    String cleanSegment = segment.replaceAll("\\[\\d+\\]", "");
    return "allOf".equals(cleanSegment) || "anyOf".equals(cleanSegment)
        || "oneOf".equals(cleanSegment);
  }

}
