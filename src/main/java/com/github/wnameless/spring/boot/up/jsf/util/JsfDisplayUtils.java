package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.util.MultiValueMap;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaForm;
import com.jayway.jsonpath.DocumentContext;
import lombok.experimental.UtilityClass;
import net.sf.rubycollect4j.Ruby;

@UtilityClass
public class JsfDisplayUtils {

  public void setDefaultFormData(JsonSchemaForm jsf, MultiValueMap<String, String> params,
      String paramName) {
    var param = params.getFirst(paramName);
    var formData = jsf.getFormData();
    formData.put(paramName, param);
    jsf.setFormData(formData);
  }

  public final class DisplayNameSetter {

    private final DocumentContext docCtx;
    private final JsonSchemaForm entity;

    public DisplayNameSetter(DocumentContext docCtx, JsonSchemaForm entity) {
      this.docCtx = docCtx;
      this.entity = entity;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public <E, ID> DisplayNameSetter addDisplayName(String fieldName, Class<E> fieldClass,
        Function<E, String> toEnumName) {
      var repo = (CrudRepository) SpringBootUp
          .findGenericBean(QuerydslPredicateExecutor.class, fieldClass).get();
      if (entity.getFormData().get(fieldName) != null) {
        JsfDisplayUtils.setEnum(docCtx, "$.properties." + fieldName,
            entity.getFormData().get(fieldName),
            toEnumName.apply((E) repo.findById((ID) entity.getFormData().get(fieldName)).get()));
      }
      return this;
    }

  }

  public <F extends JsonSchemaForm> DisplayNameSetter setDisplayNamesFor(DocumentContext docCtx,
      F entity) {
    return new DisplayNameSetter(docCtx, entity);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public <F extends JsonSchemaForm, E, ID> boolean setDisplayName(DocumentContext docCtx, F entity,
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

  public <ID> boolean setEnum(DocumentContext docCtx, String jsonPath, ID enumVal,
      String enumName) {
    if (enumVal == null || enumName == null) return false;

    docCtx.put(jsonPath, "enum", List.of(enumVal));
    docCtx.put(jsonPath, "enumNames", List.of(enumName));

    return true;
  }

  public <ID> boolean setEnum(DocumentContext docCtx, String jsonPath, ID enumVal) {
    if (enumVal == null) return false;

    docCtx.put(jsonPath, "enum", List.of(enumVal));

    return true;
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, T[] items,
      Function<T, ID> toEnum, Function<T, String> toEnumName) {
    if (items == null || items.length == 0) return false;

    return setEnum(docCtx, jsonPath, Arrays.asList(items), toEnum, toEnumName);
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, T[] items,
      Function<T, ID> toEnum) {
    if (items == null || items.length == 0) return false;

    return setEnum(docCtx, jsonPath, Arrays.asList(items), toEnum);
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Stream<T> items,
      Function<T, ID> toEnum, Function<T, String> toEnumName) {
    if (items == null) return false;
    var list = items.toList();
    if (list.isEmpty()) return false;

    return setEnum(docCtx, jsonPath, list, toEnum, toEnumName);
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Stream<T> items,
      Function<T, ID> toEnum) {
    if (items == null) return false;
    var list = items.toList();
    if (list.isEmpty()) return false;

    return setEnum(docCtx, jsonPath, list, toEnum);
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Iterable<T> items,
      Function<T, ID> toEnum, Function<T, String> toEnumName) {
    if (items == null || !items.iterator().hasNext()) return false;

    return setEnum(docCtx, jsonPath, Ruby.Array.copyOf(items), toEnum, toEnumName);
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Iterable<T> items,
      Function<T, ID> toEnum) {
    if (items == null || !items.iterator().hasNext()) return false;

    return setEnum(docCtx, jsonPath, Ruby.Array.copyOf(items), toEnum);
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Collection<T> items,
      Function<T, ID> toEnum, Function<T, String> toEnumName) {
    if (items == null || items.isEmpty()) return false;

    docCtx.put(jsonPath, "enum", items.stream().map(toEnum).toList());
    docCtx.put(jsonPath, "enumNames", items.stream().map(toEnumName).toList());

    return true;
  }

  public <T, ID> boolean setEnum(DocumentContext docCtx, String jsonPath, Collection<T> items,
      Function<T, ID> toEnum) {
    if (items == null || items.isEmpty()) return false;

    docCtx.put(jsonPath, "enum", items.stream().map(toEnum).toList());

    return true;
  }

}
