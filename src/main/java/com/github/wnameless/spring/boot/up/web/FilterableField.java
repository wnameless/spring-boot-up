package com.github.wnameless.spring.boot.up.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import lombok.Data;
import net.sf.rubycollect4j.Ruby;

@Data
public final class FilterableField<E extends EntityPathBase<?>> {

  public static <F extends EntityPathBase<?>> FilterableField<F> of(String fieldName,
      BiFunction<F, String, Predicate> filterLogic) {
    return new FilterableField<F>(f -> f, Optional.of(fieldName), "text", filterLogic);
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(String fieldName,
      BiFunction<F, String, Predicate> filterLogic, boolean sortable) {
    return new FilterableField<F>(f -> f, Optional.of(fieldName), "text", filterLogic)
        .sortable(sortable);
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(String fieldName,
      String inputType, BiFunction<F, String, Predicate> filterLogic) {
    return new FilterableField<F>(f -> f, Optional.of(fieldName), inputType, filterLogic);
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(String fieldName,
      String inputType, BiFunction<F, String, Predicate> filterLogic, boolean sortable) {
    return new FilterableField<F>(f -> f, Optional.of(fieldName), inputType, filterLogic)
        .sortable(sortable);
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(String fieldName,
      Map<String, String> selectOption, BiFunction<F, String, Predicate> filterLogic) {
    var ff = new FilterableField<F>(f -> f, Optional.of(fieldName), "text", filterLogic);
    ff.selectOption(selectOption);
    return ff;
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(String fieldName,
      Map<String, String> selectOption, BiFunction<F, String, Predicate> filterLogic,
      boolean sortable) {
    var ff = new FilterableField<F>(f -> f, Optional.of(fieldName), "text", filterLogic);
    ff.selectOption(selectOption).sortable(sortable);
    return ff;
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(String fieldName,
      List<String> datalistOption, BiFunction<F, String, Predicate> filterLogic) {
    var ff = new FilterableField<F>(f -> f, Optional.of(fieldName), "text", filterLogic);
    ff.datalistOption(datalistOption);
    return ff;
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(String fieldName,
      List<String> datalistOption, BiFunction<F, String, Predicate> filterLogic, boolean sortable) {
    var ff = new FilterableField<F>(f -> f, Optional.of(fieldName), "text", filterLogic);
    ff.datalistOption(datalistOption).sortable(sortable);
    return ff;
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(String fieldName,
      String inputType, Map<String, String> selectOption,
      BiFunction<F, String, Predicate> filterLogic) {
    var ff = new FilterableField<F>(f -> f, Optional.of(fieldName), inputType, filterLogic);
    ff.selectOption(selectOption);
    return ff;
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(String fieldName,
      String inputType, Map<String, String> selectOption,
      BiFunction<F, String, Predicate> filterLogic, boolean sortable) {
    var ff = new FilterableField<F>(f -> f, Optional.of(fieldName), inputType, filterLogic);
    ff.selectOption(selectOption).sortable(sortable);
    return ff;
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(
      Function<F, ? extends Path<?>> pathFinder, BiFunction<F, String, Predicate> filterLogic) {
    return new FilterableField<F>(pathFinder, Optional.empty(), "text", filterLogic);
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(
      Function<F, ? extends Path<?>> pathFinder, BiFunction<F, String, Predicate> filterLogic,
      boolean sortable) {
    return new FilterableField<F>(pathFinder, Optional.empty(), "text", filterLogic)
        .sortable(sortable);
  }

  private final Function<E, ? extends Path<?>> pathFinder;
  private final Optional<String> fieldName;
  private final String inputType;
  private final BiFunction<E, String, Predicate> filterLogic;
  private boolean sortable = true;
  private final LinkedHashMap<String, String> selectOption = new LinkedHashMap<>();
  private final List<String> datalistOption = new ArrayList<>();
  private final Map<String, String> attr = new LinkedHashMap<>();

  public String getAttrString() {
    return Ruby.Array.copyOf(getAttr().entrySet()).map(e -> e.getKey() + "=" + e.getValue())
        .join(", ");
  }

  public Map<String, String> getAttr() {
    var copy = new LinkedHashMap<String, String>();
    copy.put("type", inputType);
    copy.putAll(attr);
    return copy;
  }

  public FilterableField<E> attr(Map<String, String> attr) {
    this.attr.putAll(attr);
    return this;
  }

  public FilterableField<E> sortable(boolean sortable) {
    this.sortable = sortable;
    return this;
  }

  public FilterableField<E> selectOption(Map<String, String> selectOption) {
    this.selectOption.putAll(selectOption);
    return this;
  }

  public boolean hasDatalistOption() {
    return !datalistOption.isEmpty();
  }

  public FilterableField<E> datalistOption(List<String> datalistOption) {
    this.datalistOption.addAll(datalistOption);
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
    return entryFieldName.replace(path.getRoot().toString() + ".", "");
  }

}
