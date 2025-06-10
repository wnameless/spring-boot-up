package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.MultiValueMap;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaForm;
import com.jayway.jsonpath.DocumentContext;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsfDisplayUtils {

  public void setDefaultFormData(JsonSchemaForm jsf, MultiValueMap<String, String> params,
      String paramName) {
    var param = params.getFirst(paramName);
    var formData = jsf.getFormData();
    formData.put(paramName, param);
    jsf.setFormData(formData);
  }

  public <F extends JsonSchemaForm, E, ID> boolean setDisplayEnum(DocumentContext docCtx, F entity,
      String fieldName, Class<E> fieldClass) {
    var repoOpt = SpringBootUp.findGenericBean(QuerydslPredicateExecutor.class, fieldClass);
    if (repoOpt.isPresent() && entity.getFormData().get(fieldName) != null) {
      JsfDisplayUtils.setEnum(docCtx, "$.properties." + fieldName,
          entity.getFormData().get(fieldName));

      return true;
    }

    return false;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public <F extends JsonSchemaForm, E, ID> boolean setDisplayEnum(DocumentContext docCtx, F entity,
      String fieldName, Class<E> fieldClass, Function<E, String> toEnumName) {
    var repoOpt = SpringBootUp.findGenericBean(QuerydslPredicateExecutor.class, fieldClass);
    if (repoOpt.isPresent() && entity.getFormData().get(fieldName) != null) {
      JsfDisplayUtils.setEnum(docCtx, "$.properties." + fieldName,
          entity.getFormData().get(fieldName), toEnumName.apply((E) ((CrudRepository) repoOpt.get())
              .findById((ID) entity.getFormData().get(fieldName)).get()));

      return true;
    }

    return false;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public <F extends JsonSchemaForm, E, ID> boolean setDisplayUiEnumName(DocumentContext uiDocCtx,
      F entity, String fieldName, Class<E> fieldClass, Function<E, String> toEnumName) {
    var repoOpt = SpringBootUp.findGenericBean(QuerydslPredicateExecutor.class, fieldClass);
    if (repoOpt.isPresent() && entity.getFormData().get(fieldName) != null) {
      var enumName = toEnumName.apply((E) ((CrudRepository) repoOpt.get())
          .findById((ID) entity.getFormData().get(fieldName)).get());
      JsfDisplayUtils.setUiEnumNames(uiDocCtx, "$.properties." + fieldName, List.of(enumName));

      return true;
    }

    return false;
  }

  public <ID> boolean setEnum(DocumentContext docCtx, String jsonPath, ID enumVal) {
    if (enumVal == null) return false;

    docCtx.put(jsonPath, "enum", List.of(enumVal));

    return true;
  }

  public <ID> boolean setEnum(DocumentContext docCtx, String jsonPath, ID enumVal,
      String enumName) {
    if (enumVal == null || enumName == null) return false;

    docCtx.put(jsonPath, "enum", List.of(enumVal));
    docCtx.put(jsonPath, "enumNames", List.of(enumName));

    return true;
  }

  public <ID> boolean setEnum(DocumentContext docCtx, String jsonPath, ID enumVal, String enumName,
      DocumentContext uiDocCtx) {
    return setEnum(docCtx, jsonPath, enumVal, enumName, uiDocCtx, false);
  }

  public <ID> boolean setEnum(DocumentContext docCtx, String jsonPath, ID enumVal, String enumName,
      DocumentContext uiDocCtx, boolean supportEnumNames) {
    if (enumVal == null || enumName == null) return false;

    docCtx.put(jsonPath, "enum", List.of(enumVal));
    if (supportEnumNames) {
      docCtx.put(jsonPath, "enumNames", List.of(enumName));
    }
    forceCreateAndPut(uiDocCtx, jsonPathToUiPath(jsonPath), "ui:enumNames", List.of(enumName));

    return true;
  }

  public <ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Collection<ID> enumValColl) {
    if (enumValColl == null || enumValColl.isEmpty()) return false;

    docCtx.put(jsonPath, "enum", enumValColl);

    return true;
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Collection<T> items,
      Function<T, ID> toEnum) {
    if (items == null || items.isEmpty()) return false;

    docCtx.put(jsonPath, "enum", items.stream().map(toEnum).toList());

    return true;
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Collection<T> items,
      Function<T, ID> toEnum, Function<T, String> toEnumName) {
    if (items == null || items.isEmpty()) return false;

    docCtx.put(jsonPath, "enum", items.stream().map(toEnum).toList());
    docCtx.put(jsonPath, "enumNames", items.stream().map(toEnumName).toList());

    return true;
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Collection<T> items,
      Function<T, ID> toEnum, Function<T, String> toEnumName, DocumentContext uiDocCtx) {
    return setEnum(docCtx, jsonPath, items, toEnum, toEnumName, uiDocCtx, false);
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Collection<T> items,
      Function<T, ID> toEnum, Function<T, String> toEnumName, DocumentContext uiDocCtx,
      boolean supportEnumNames) {
    if (items == null || items.isEmpty()) return false;

    docCtx.put(jsonPath, "enum", items.stream().map(toEnum).toList());
    if (supportEnumNames) {
      docCtx.put(jsonPath, "enumNames", items.stream().map(toEnumName).toList());
    }
    forceCreateAndPut(uiDocCtx, jsonPathToUiPath(jsonPath), "ui:enumNames",
        items.stream().map(toEnumName).toList());

    return true;
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Collection<ID> enums,
      Collection<T> enumNames) {
    if (enums == null || enums.isEmpty()) return false;
    if (enumNames == null || enumNames.isEmpty()) return false;

    docCtx.put(jsonPath, "enum", enums);
    docCtx.put(jsonPath, "enumNames", enumNames);

    return true;
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Collection<ID> enums,
      Collection<T> enumNames, DocumentContext uiDocCtx) {
    return setEnum(docCtx, jsonPath, enums, enumNames, uiDocCtx, false);
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Collection<ID> enums,
      Collection<T> enumNames, DocumentContext uiDocCtx, boolean supportEnumNames) {
    if (enums == null || enums.isEmpty()) return false;
    if (enumNames == null || enumNames.isEmpty()) return false;

    docCtx.put(jsonPath, "enum", enums);
    if (supportEnumNames) {
      docCtx.put(jsonPath, "enumNames", enumNames);
    }
    forceCreateAndPut(uiDocCtx, jsonPathToUiPath(jsonPath), "ui:enumNames", enumNames);

    return true;
  }

  public <T, ID> boolean setUiEnumNames(DocumentContext uiDocCtx, String jsonPath,
      Collection<T> enumNames) {
    if (enumNames == null || enumNames.isEmpty()) return false;

    forceCreateAndPut(uiDocCtx, jsonPathToUiPath(jsonPath), "ui:enumNames", enumNames);

    return true;
  }

  public <T, ID> boolean setUiEnumNames(DocumentContext uiDocCtx, String jsonPath,
      Collection<T> enums, Function<T, String> toEnumName) {
    if (enums == null || enums.isEmpty()) return false;

    forceCreateAndPut(uiDocCtx, jsonPathToUiPath(jsonPath), "ui:enumNames",
        enums.stream().map(toEnumName).toList());

    return true;
  }

  private String jsonPathToUiPath(String jsonPath) {
    return jsonPath.replaceAll(Pattern.quote(".properties"), "");
  }

  @SuppressWarnings("unchecked")
  private void forceCreateAndPut(DocumentContext docCtx, String jsonPath, String key,
      Object value) {
    // Remove the trailing property (like '.baz')
    String[] parts = jsonPath.replaceFirst("^\\$\\.", "").split("\\.");
    Map<String, Object> current = docCtx.json();

    for (int i = 0; i < parts.length; i++) {
      String part = parts[i];
      if (i == parts.length - 1) {
        // Last part, put value
        if (current.get(part) == null || !(current.get(part) instanceof Map)) {
          current.put(part, new LinkedHashMap<String, Object>());
        }
        ((Map<String, Object>) current.get(part)).put(key, value);
      } else {
        // Traverse/create intermediate objects
        if (current.get(part) == null || !(current.get(part) instanceof Map)) {
          current.put(part, new LinkedHashMap<String, Object>());
        }
        current = (Map<String, Object>) current.get(part);
      }
    }
  }

}
