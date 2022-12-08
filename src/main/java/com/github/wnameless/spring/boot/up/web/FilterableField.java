package com.github.wnameless.spring.boot.up.web;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;

import lombok.Data;

@Data
public final class FilterableField<E extends EntityPathBase<?>> {

  public static <F extends EntityPathBase<?>> FilterableField<F> of(
      String fieldName,
      BiFunction<F, String, Predicate> filterLogic) {
    return new FilterableField<F>(f -> f, Optional.of(fieldName), filterLogic);
  }

  public static <F extends EntityPathBase<?>> FilterableField<F> of(
      Function<F, ? extends Path<?>> pathFinder,
      BiFunction<F, String, Predicate> filterLogic) {
    return new FilterableField<F>(pathFinder, Optional.empty(), filterLogic);
  }

  private final Function<E, ? extends Path<?>> pathFinder;
  private final Optional<String> field;
  private final BiFunction<E, String, Predicate> filterLogic;

  public Function<String, Predicate> getLogic(E qEntity) {
    return param -> filterLogic.apply(qEntity, param);
  }

  public String getFieldName(E qEntity) {
    return field.isPresent() ? field.get() : pathFinder.apply(qEntity).getMetadata().getName();
  }

}
