package com.github.wnameless.spring.boot.up.jsf.util;

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
    public <E, ID> DisplayNameSetter addDisplayName(String dataKey, Class<E> klass,
        Function<E, String> toEnumName) {
      var repo = (CrudRepository) SpringBootUp
          .findGenericBean(QuerydslPredicateExecutor.class, klass).get();
      if (entity.getFormData().get(dataKey) != null) {
        JsfDisplayUtils.setEnum(docCtx, "$.properties." + dataKey,
            entity.getFormData().get(dataKey),
            toEnumName.apply((E) repo.findById((ID) entity.getFormData().get(dataKey)).get()));
      }
      return this;
    }

  }

  public <F extends JsonSchemaForm> DisplayNameSetter setDisplayNamesFor(DocumentContext docCtx,
      F entity) {
    return new DisplayNameSetter(docCtx, entity);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public <F extends JsonSchemaForm, E, ID> DocumentContext setDisplayName(DocumentContext docCtx,
      F entity, String dataKey, Class<E> klass, Function<E, String> toEnumName) {
    var repo =
        (CrudRepository) SpringBootUp.findGenericBean(QuerydslPredicateExecutor.class, klass).get();
    if (entity.getFormData().get(dataKey) != null) {
      JsfDisplayUtils.setEnum(docCtx, "$.properties." + dataKey, entity.getFormData().get(dataKey),
          toEnumName.apply((E) repo.findById((ID) entity.getFormData().get(dataKey)).get()));
    }

    return docCtx;
  }

  public <ID> DocumentContext setEnum(DocumentContext docCtx, String jsonPath, ID enumVal,
      String enumName) {
    docCtx.put(jsonPath, "enum", List.of(enumVal));
    docCtx.put(jsonPath, "enumNames", List.of(enumName));
    return docCtx;
  }

  public <T, ID> DocumentContext setEnum(DocumentContext docCtx, String jsonPath, Stream<T> items,
      Function<T, ID> toEnum, Function<T, String> toEnumName) {
    return setEnum(docCtx, jsonPath, items.toList(), toEnum, toEnumName);
  }

  public <T, ID> DocumentContext setEnum(DocumentContext docCtx, String jsonPath, Iterable<T> items,
      Function<T, ID> toEnum, Function<T, String> toEnumName) {
    return setEnum(docCtx, jsonPath, Ruby.Array.copyOf(items), toEnum, toEnumName);
  }

  public <T, ID> DocumentContext setEnum(DocumentContext docCtx, String jsonPath,
      Collection<T> items, Function<T, ID> toEnum, Function<T, String> toEnumName) {
    docCtx.put(jsonPath, "enum", items.stream().map(toEnum).toList());
    docCtx.put(jsonPath, "enumNames", items.stream().map(toEnumName).toList());
    return docCtx;
  }

}
