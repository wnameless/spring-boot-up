/*
 *
 * Copyright 2022 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.spring.boot.up.web;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import com.github.wnameless.spring.boot.up.web.Pageables.PageableParams;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.StringPath;

import lombok.Data;

@Data
public final class QuerySetting<E extends EntityPathBase<?>> {

  public static <E extends EntityPathBase<?>> QuerySetting<E> of(E entity) {
    return new QuerySetting<E>(entity);
  }

  private final E entity;
  private final Map<String, FilterableField<E>> filterFields;
  private final PageableParams pageableParams = PageableParams.ofSpring();

  public QuerySetting(E entity) {
    this.entity = entity;
    this.filterFields = new LinkedHashMap<>();
  }

  public QuerySetting(E entity, Map<String, FilterableField<E>> filterFields) {
    this.entity = entity;
    this.filterFields = filterFields;
  }

  public StringPathFilterableField filterableField(Function<E, StringPath> pathFinder) {
    return new StringPathFilterableField(pathFinder);
  }

  public QuerySetting<E> filterableField(FilterableField<E> ff) {
    filterFields.put(ff.getFieldName(entity), ff);
    return this;
  }

  public QuerySetting<E> filterableField(
      Function<E, ? extends Path<?>> pathFinder,
      BiFunction<E, String, Predicate> filterLogic) {
    FilterableField<E> ff = FilterableField.of(pathFinder, filterLogic);
    filterFields.put(ff.getFieldName(entity), ff);
    return this;
  }

  public QuerySetting<E> filterableField(
      String field,
      BiFunction<E, String, Predicate> filterLogic) {
    FilterableField<E> ff = FilterableField.of(field, filterLogic);
    filterFields.put(ff.getFieldName(entity), ff);
    return this;
  }

  @Data
  public final class StringPathFilterableField {

    private final Function<E, StringPath> pathFinder;
    private String alias;

    public StringPathFilterableField alias(String alias) {
      this.alias = alias;
      return this;
    }

    public QuerySetting<E> containsIgnoreCase() {
      if (alias != null) {
        QuerySetting.this.filterableField(alias, (e, param) -> pathFinder.apply(e).containsIgnoreCase(param));
      } else {
        QuerySetting.this.filterableField(pathFinder, (e, param) -> pathFinder.apply(e).containsIgnoreCase(param));
      }
      return QuerySetting.this;
    }

  }

}
