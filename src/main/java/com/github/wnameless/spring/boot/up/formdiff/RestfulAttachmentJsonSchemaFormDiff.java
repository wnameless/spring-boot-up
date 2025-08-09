package com.github.wnameless.spring.boot.up.formdiff;

import static lombok.AccessLevel.PRIVATE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.attachment.Attachment;
import com.github.wnameless.spring.boot.up.jsf.RestfulAttachmentJsonSchemaForm;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class RestfulAttachmentJsonSchemaFormDiff<ID> {

  final RestfulAttachmentJsonSchemaForm<? extends Attachment<ID>, ID> beforeForm;
  final RestfulAttachmentJsonSchemaForm<? extends Attachment<ID>, ID> afterForm;

  boolean diffCalculated = false;
  RestfulAttachmentJsonSchemaForm<? extends Attachment<ID>, ID> beforeFormDiff;
  RestfulAttachmentJsonSchemaForm<? extends Attachment<ID>, ID> afterFormDiff;

  final ObjectMapper objectMapper = new ObjectMapper();

  public synchronized void calculateDiff() {
    if (diffCalculated) {
      return;
    }

    // Create deep copies of the forms
    beforeFormDiff = beforeForm.deepCopy();
    afterFormDiff = afterForm.deepCopy();

    // Get formData from both forms
    Map<String, Object> beforeData = beforeForm.getFormData();
    Map<String, Object> afterData = afterForm.getFormData();

    // Get uiSchemas
    Map<String, Object> beforeUiSchema = new LinkedHashMap<>(beforeFormDiff.getUiSchema());
    Map<String, Object> afterUiSchema = new LinkedHashMap<>(afterFormDiff.getUiSchema());

    // Collect all field paths
    Set<String> allFields = new HashSet<>();
    collectFieldPaths(beforeData, "", allFields);
    collectFieldPaths(afterData, "", allFields);

    // Compare each field
    for (String fieldPath : allFields) {
      Object beforeValue = getValueByPath(beforeData, fieldPath);
      Object afterValue = getValueByPath(afterData, fieldPath);

      FormDiffColor color = determineColor(beforeValue, afterValue);

      if (color != FormDiffColor.NONE) {
        String cssClass = mapColorToCssClass(color);

        // Apply color to beforeForm if field exists there
        if (beforeValue != null) {
          applyColorToUiSchema(beforeUiSchema, fieldPath, cssClass);
        }

        // Apply color to afterForm if field exists there
        if (afterValue != null) {
          // For additions, use SUCCESS color in afterForm
          if (beforeValue == null) {
            applyColorToUiSchema(afterUiSchema, fieldPath,
                mapColorToCssClass(FormDiffColor.SUCCESS));
          } else {
            applyColorToUiSchema(afterUiSchema, fieldPath, cssClass);
          }
        }
      }
    }

    // Update the uiSchemas
    beforeFormDiff.setUiSchema(beforeUiSchema);
    afterFormDiff.setUiSchema(afterUiSchema);

    // Compare attachments
    compareAttachments();

    diffCalculated = true;
  }

  @SuppressWarnings("rawtypes")
  private void compareAttachments() {
    if (beforeFormDiff.getAttachmentSnapshot() == null
        || afterFormDiff.getAttachmentSnapshot() == null) {
      return;
    }

    List<? extends Attachment> beforeAttachments =
        beforeFormDiff.getAttachmentSnapshot().getAttachments();
    List<? extends Attachment> afterAttachments =
        afterFormDiff.getAttachmentSnapshot().getAttachments();

    if (beforeAttachments == null) beforeAttachments = new ArrayList<>();
    if (afterAttachments == null) afterAttachments = new ArrayList<>();

    Set<Attachment> processedInAfterForm = new HashSet<>();

    // First pass: process all attachments from beforeForm
    for (Attachment beforeAttachment : beforeAttachments) {
      Attachment matchingAttachment = findMatchingAttachment(beforeAttachment, afterAttachments);

      if (matchingAttachment == null) {
        // Attachment deleted - only exists in beforeForm
        applyAttachmentColor(beforeAttachment, FormDiffColor.DANGER);
      } else {
        processedInAfterForm.add(matchingAttachment);

        // Compare attachment properties
        FormDiffColor color = determineAttachmentColor(beforeAttachment, matchingAttachment);
        if (color != FormDiffColor.NONE) {
          applyAttachmentColor(beforeAttachment, color);
          applyAttachmentColor(matchingAttachment, color);
        }
      }
    }

    // Second pass: process remaining attachments in afterForm (new ones)
    for (Attachment afterAttachment : afterAttachments) {
      if (!processedInAfterForm.contains(afterAttachment)) {
        // New attachment - only exists in afterForm
        applyAttachmentColor(afterAttachment, FormDiffColor.SUCCESS);
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private Attachment findMatchingAttachment(Attachment target,
      List<? extends Attachment> attachments) {
    if (target == null || attachments == null) {
      return null;
    }

    // First try to match by ID
    if (target.getId() != null) {
      for (Attachment attachment : attachments) {
        if (Objects.equals(target.getId(), attachment.getId())) {
          return attachment;
        }
      }
    }

    // If no ID match, try to match by group + name combination
    if (target.getGroup() != null && target.getName() != null) {
      for (Attachment attachment : attachments) {
        if (Objects.equals(target.getGroup(), attachment.getGroup())
            && Objects.equals(target.getName(), attachment.getName())) {
          return attachment;
        }
      }
    }

    return null;
  }

  @SuppressWarnings("rawtypes")
  private FormDiffColor determineAttachmentColor(Attachment before, Attachment after) {
    if (before == null || after == null) {
      return FormDiffColor.NONE;
    }

    // Check if group changed (structural change)
    if (!Objects.equals(before.getGroup(), after.getGroup())) {
      return FormDiffColor.WARNING;
    }

    // Check if URI changed (file updated - most common case)
    if (!Objects.equals(before.getUri(), after.getUri())) {
      return FormDiffColor.INFO;
    }

    // Check if note changed (minor change)
    if (!Objects.equals(before.getNote(), after.getNote())) {
      return FormDiffColor.INFO;
    }

    // Check if name changed (could be important)
    if (!Objects.equals(before.getName(), after.getName())) {
      return FormDiffColor.INFO;
    }

    // No changes
    return FormDiffColor.NONE;
  }

  @SuppressWarnings("rawtypes")
  private void applyAttachmentColor(Attachment attachment, FormDiffColor color) {
    if (attachment == null || color == FormDiffColor.NONE) {
      return;
    }

    String cssClass = mapColorToCssClass(color);
    String existingClasses = attachment.getUiClassNames();
    String mergedClasses = mergeClassNames(existingClasses, cssClass);
    attachment.setUiClassNames(mergedClasses);
  }

  public RestfulAttachmentJsonSchemaForm<?, ID> getBeforeFormDiff() {
    if (!diffCalculated) {
      calculateDiff();
    }
    return beforeFormDiff;
  }

  public RestfulAttachmentJsonSchemaForm<?, ID> getAfterFormDiff() {
    if (!diffCalculated) {
      calculateDiff();
    }
    return afterFormDiff;
  }

  private void collectFieldPaths(Map<String, Object> data, String prefix, Set<String> paths) {
    if (data == null) return;

    for (Map.Entry<String, Object> entry : data.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      String currentPath = prefix.isEmpty() ? key : prefix + "." + key;

      if (value instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> nestedMap = (Map<String, Object>) value;
        if (nestedMap.isEmpty()) {
          // Empty object is considered a leaf
          paths.add(currentPath);
        } else {
          // Recurse into nested object - don't add the container path
          collectFieldPaths(nestedMap, currentPath, paths);
        }
      } else if (value instanceof List) {
        @SuppressWarnings("unchecked")
        List<Object> list = (List<Object>) value;
        if (list.isEmpty()) {
          // Empty array is considered a leaf
          paths.add(currentPath);
        } else {
          // Process array items with index notation
          for (int i = 0; i < list.size(); i++) {
            Object item = list.get(i);
            String itemPath = currentPath + "[" + i + "]";
            if (isLeafValue(item)) {
              paths.add(itemPath);
            } else if (item instanceof Map) {
              @SuppressWarnings("unchecked")
              Map<String, Object> itemMap = (Map<String, Object>) item;
              collectFieldPaths(itemMap, itemPath, paths);
            } else if (item instanceof List) {
              // Nested array
              collectArrayPaths((List<?>) item, itemPath, paths);
            }
          }
        }
      } else {
        // Leaf value (primitive, null, or other non-container type)
        paths.add(currentPath);
      }
    }
  }

  private void collectArrayPaths(List<?> list, String prefix, Set<String> paths) {
    if (list.isEmpty()) {
      paths.add(prefix);
      return;
    }

    for (int i = 0; i < list.size(); i++) {
      Object item = list.get(i);
      String itemPath = prefix + "[" + i + "]";
      if (isLeafValue(item)) {
        paths.add(itemPath);
      } else if (item instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> itemMap = (Map<String, Object>) item;
        collectFieldPaths(itemMap, itemPath, paths);
      } else if (item instanceof List) {
        // Recursively handle nested arrays
        collectArrayPaths((List<?>) item, itemPath, paths);
      }
    }
  }

  private boolean isLeafValue(Object value) {
    if (value == null) return true;
    return !(value instanceof Map || value instanceof List);
  }

  private Object getValueByPath(Map<String, Object> data, String path) {
    if (data == null || path == null || path.isEmpty()) {
      return null;
    }

    String[] parts = path.split("\\.");
    Object current = data;

    for (String part : parts) {
      // Check if this part contains array index notation
      if (part.contains("[")) {
        // Extract the field name and indices
        String fieldName = part.substring(0, part.indexOf("["));
        String remaining = part.substring(part.indexOf("["));

        // First get the field
        if (current instanceof Map) {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = (Map<String, Object>) current;
          current = map.get(fieldName);
          if (current == null) {
            return null;
          }
        } else {
          return null;
        }

        // Then process array indices
        while (remaining.startsWith("[")) {
          int endIdx = remaining.indexOf("]");
          if (endIdx == -1) break;

          String indexStr = remaining.substring(1, endIdx);
          int index = Integer.parseInt(indexStr);

          if (current instanceof List) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) current;
            if (index >= 0 && index < list.size()) {
              current = list.get(index);
            } else {
              return null;
            }
          } else {
            return null;
          }

          remaining = remaining.substring(endIdx + 1);
        }
      } else {
        // Regular field access
        if (current instanceof Map) {
          @SuppressWarnings("unchecked")
          Map<String, Object> map = (Map<String, Object>) current;
          current = map.get(part);
          if (current == null) {
            return null;
          }
        } else {
          return null;
        }
      }
    }

    return current;
  }

  private FormDiffColor determineColor(Object beforeValue, Object afterValue) {
    // Field deleted (exists in before, not in after)
    if (beforeValue != null && afterValue == null) {
      return FormDiffColor.DANGER;
    }

    // Field added (not in before, exists in after)
    if (beforeValue == null && afterValue != null) {
      return FormDiffColor.SUCCESS;
    }

    // Both null, no change
    if (beforeValue == null && afterValue == null) {
      return FormDiffColor.NONE;
    }

    // Check if values are equal
    if (Objects.equals(beforeValue, afterValue)) {
      return FormDiffColor.NONE;
    }

    // Values are different, check for type change
    if (detectTypeChange(beforeValue, afterValue)) {
      return FormDiffColor.WARNING;
    }

    // Same type but different value
    return FormDiffColor.INFO;
  }

  private boolean detectTypeChange(Object beforeValue, Object afterValue) {
    if (beforeValue == null || afterValue == null) {
      return false;
    }

    // Get the basic type classes
    Class<?> beforeClass = getBasicType(beforeValue);
    Class<?> afterClass = getBasicType(afterValue);

    return !beforeClass.equals(afterClass);
  }

  private Class<?> getBasicType(Object value) {
    if (value instanceof Number) {
      return Number.class;
    } else if (value instanceof String) {
      return String.class;
    } else if (value instanceof Boolean) {
      return Boolean.class;
    } else if (value instanceof Map) {
      return Map.class;
    } else if (value instanceof Iterable) {
      return Iterable.class;
    }
    return value.getClass();
  }

  private String mapColorToCssClass(FormDiffColor color) {
    switch (color) {
      case DANGER:
        return "bg-danger";
      case SUCCESS:
        return "bg-success";
      case WARNING:
        return "bg-warning";
      case INFO:
        return "bg-info";
      case NONE:
      default:
        return "";
    }
  }

  private void applyColorToUiSchema(Map<String, Object> uiSchema, String fieldPath,
      String cssClass) {
    if (cssClass == null || cssClass.isEmpty()) {
      return;
    }

    // For nested fields, we need to navigate the uiSchema structure
    String[] parts = fieldPath.split("\\.");
    Map<String, Object> current = uiSchema;

    for (int i = 0; i < parts.length; i++) {
      String part = parts[i];

      // Check if this part contains array index notation
      if (part.contains("[")) {
        // For arrays in uiSchema, we need to handle them differently
        // Extract the field name
        String fieldName = part.substring(0, part.indexOf("["));

        // Extract all array indices from this part
        String remaining = part.substring(part.indexOf("["));
        String indices = "";
        while (remaining.startsWith("[")) {
          int endIdx = remaining.indexOf("]");
          if (endIdx == -1) break;
          indices += remaining.substring(0, endIdx + 1);
          remaining = remaining.substring(endIdx + 1);
        }

        // In uiSchema, array items are typically configured with "items" property
        if (i == parts.length - 1) {
          // Last part - apply to specific array item
          @SuppressWarnings("unchecked")
          Map<String, Object> arrayUiSchema = (Map<String, Object>) current.get(fieldName);
          if (arrayUiSchema == null) {
            arrayUiSchema = new HashMap<>();
            current.put(fieldName, arrayUiSchema);
          }

          // For array items, we typically use "items" to apply styles
          // Since we want item-specific styling, we'll add it directly with the index
          String itemKey = "items" + indices;
          @SuppressWarnings("unchecked")
          Map<String, Object> itemUiSchema = (Map<String, Object>) arrayUiSchema.get(itemKey);
          if (itemUiSchema == null) {
            itemUiSchema = new HashMap<>();
            arrayUiSchema.put(itemKey, itemUiSchema);
          }

          String existingClasses = (String) itemUiSchema.get("ui:classNames");
          String mergedClasses = mergeClassNames(existingClasses, cssClass);
          itemUiSchema.put("ui:classNames", mergedClasses);
        } else {
          // Navigate deeper through array
          @SuppressWarnings("unchecked")
          Map<String, Object> arrayUiSchema = (Map<String, Object>) current.get(fieldName);
          if (arrayUiSchema == null) {
            arrayUiSchema = new HashMap<>();
            current.put(fieldName, arrayUiSchema);
          }

          // For navigation, we need to go through items structure
          String itemKey = "items" + indices;
          @SuppressWarnings("unchecked")
          Map<String, Object> itemUiSchema = (Map<String, Object>) arrayUiSchema.get(itemKey);
          if (itemUiSchema == null) {
            itemUiSchema = new HashMap<>();
            arrayUiSchema.put(itemKey, itemUiSchema);
          }
          current = itemUiSchema;
        }
      } else {
        // Regular field handling
        if (i == parts.length - 1) {
          // Last part, apply the CSS class
          @SuppressWarnings("unchecked")
          Map<String, Object> fieldUiSchema = (Map<String, Object>) current.get(part);
          if (fieldUiSchema == null) {
            fieldUiSchema = new HashMap<>();
            current.put(part, fieldUiSchema);
          }

          // Merge with existing ui:classNames
          String existingClasses = (String) fieldUiSchema.get("ui:classNames");
          String mergedClasses = mergeClassNames(existingClasses, cssClass);
          fieldUiSchema.put("ui:classNames", mergedClasses);
        } else {
          // Navigate deeper
          @SuppressWarnings("unchecked")
          Map<String, Object> nestedSchema = (Map<String, Object>) current.get(part);
          if (nestedSchema == null) {
            nestedSchema = new HashMap<>();
            current.put(part, nestedSchema);
          }
          current = nestedSchema;
        }
      }
    }
  }

  private String mergeClassNames(String existing, String newClass) {
    if (existing == null || existing.trim().isEmpty()) {
      return newClass;
    }

    // Split existing classes and check if new class already exists
    Set<String> classes = Stream.of(existing.trim().split("\\s+")).collect(Collectors.toSet());

    // Add new class if not already present
    if (!classes.contains(newClass)) {
      classes.add(newClass);
    }

    // Join all classes with space
    return String.join(" ", classes);
  }

}
