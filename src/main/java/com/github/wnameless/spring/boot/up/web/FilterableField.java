package com.github.wnameless.spring.boot.up.web;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.Data;

@Data
public final class FilterableField<E extends EntityPathBase<?>> {

  public static <F extends EntityPathBase<?>> FilterableField<F> of(String fieldName,
      BiFunction<F, String, Predicate> filterLogic) {
    return new FilterableField<F>(f -> f, Optional.of(fieldName), filterLogic);
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(
      Function<F, ? extends Path<?>> pathFinder, BiFunction<F, String, Predicate> filterLogic) {
    return new FilterableField<F>(pathFinder, Optional.empty(), filterLogic);
  }

  private final Function<E, ? extends Path<?>> pathFinder;
  private final Optional<String> fieldName;
  private final BiFunction<E, String, Predicate> filterLogic;
  private boolean sortable = true;
  private final LinkedHashMap<String, String> selectOption = new LinkedHashMap<>();

  public FilterableField<E> sortable(boolean sortable) {
    this.sortable = sortable;
    return this;
  }

  public FilterableField<E> selectOption(Map<String, String> selectOption) {
    this.selectOption.putAll(selectOption);
    return this;
  }

  public boolean hasSelectOption() {
    return !selectOption.isEmpty();
  }

  public Function<String, Predicate> getFilterLogic(E qEntity) {
    return param -> filterLogic.apply(qEntity, param);
  }

  public String getFieldName(E qEntity) {
    return fieldName.orElse(getEntryFieldName(pathFinder.apply(qEntity)));
  }

  public String getSortableFieldName(E qEntity) {
    if (!sortable) return null;
    return getEntryFieldName(pathFinder.apply(qEntity));
  }

  private String getEntryFieldName(Path<?> path) {
    String entryFieldName = path.toString();
    return entryFieldName.substring(entryFieldName.indexOf('.') + 1);
  }

}
