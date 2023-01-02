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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
  private final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
  private PageableParams pageableParams = PageableParams.ofSpring();

  public QuerySetting(E entity) {
    this.entity = entity;
    this.filterFields = new LinkedHashMap<>();
  }

  public QuerySetting<E> defaultSort(String... fields) {
    Arrays.asList(fields).forEach(f -> params.add(pageableParams.getSortParameter(), f));
    return this;
  }

  public QuerySetting<E> defaultSort(Path<?>... paths) {
    Arrays.asList(paths)
        .forEach(p -> params.add(pageableParams.getSortParameter(), p.getMetadata().getName()));
    return this;
  }

  public QuerySetting<E> defaultSort(Sort sort) {
    sort.forEach(o -> params.add(pageableParams.getSortParameter(),
        o.getProperty() + "," + o.getDirection()));
    return this;
  }

  public MultiValueMap<String, String> defaultParams(MultiValueMap<String, String> other) {
    params.entrySet().forEach(e -> {
      other.putIfAbsent(e.getKey(), e.getValue());
    });
    return other;
  }

  public QuerySetting<E> addFilterableField(FilterableField<E> ff) {
    filterFields.put(ff.getFieldName(entity), ff);
    return this;
  }

  public PathFilterableField onField(Function<E, Path<?>> pathFinder) {
    return new PathFilterableField(pathFinder);
  }

  public StringPathFilterableField onStringField(Function<E, StringPath> pathFinder) {
    return new StringPathFilterableField(pathFinder);
  }

  @Data
  public final class PathFilterableField {

    private final Function<E, Path<?>> pathFinder;
    private String alias;
    private boolean sortable = true;

    public PathFilterableField alias(String alias) {
      this.alias = alias;
      return this;
    }

    public PathFilterableField sortable(boolean sortable) {
      this.sortable = sortable;
      return this;
    }

    public QuerySetting<E> filterLogic(BiFunction<E, String, Predicate> filterLogic) {
      QuerySetting.this.addFilterableField(
          new FilterableField<>(pathFinder, Optional.ofNullable(alias), filterLogic)
              .sortable(sortable));
      return QuerySetting.this;
    }

  }

  @Data
  public final class StringPathFilterableField {

    private final Function<E, StringPath> pathFinder;
    private String alias;
    private boolean sortable = true;

    public StringPathFilterableField alias(String alias) {
      this.alias = alias;
      return this;
    }

    public StringPathFilterableField sortable(boolean sortable) {
      this.sortable = sortable;
      return this;
    }

    public QuerySetting<E> filterLogic(BiFunction<E, String, Predicate> filterLogic) {
      QuerySetting.this.addFilterableField(
          new FilterableField<>(pathFinder, Optional.ofNullable(alias), filterLogic)
              .sortable(sortable));
      return QuerySetting.this;
    }

    public QuerySetting<E> filterByContainsIgnoreCase() {
      QuerySetting.this
          .addFilterableField(new FilterableField<>(pathFinder, Optional.ofNullable(alias),
              (e, param) -> pathFinder.apply(e).containsIgnoreCase(param)).sortable(sortable));
      return QuerySetting.this;
    }

    public QuerySetting<E> filterByContains() {
      QuerySetting.this
          .addFilterableField(new FilterableField<>(pathFinder, Optional.ofNullable(alias),
              (e, param) -> pathFinder.apply(e).contains(param)).sortable(sortable));
      return QuerySetting.this;
    }

    public QuerySetting<E> filterByEq() {
      QuerySetting.this.addFilterableField(
          new FilterableField<>(pathFinder, Optional.ofNullable(alias), (e, param) -> {
            return param == null || param.trim().isEmpty()
                ? pathFinder.apply(e).containsIgnoreCase(param)
                : pathFinder.apply(e).eq(param);
          }).sortable(sortable));
      return QuerySetting.this;
    }

    public QuerySetting<E> filterByStartsWith() {
      QuerySetting.this
          .addFilterableField(new FilterableField<>(pathFinder, Optional.ofNullable(alias),
              (e, param) -> pathFinder.apply(e).startsWith(param)).sortable(sortable));
      return QuerySetting.this;
    }

    public QuerySetting<E> filterByEndsWith() {
      QuerySetting.this
          .addFilterableField(new FilterableField<>(pathFinder, Optional.ofNullable(alias),
              (e, param) -> pathFinder.apply(e).endsWith(param)).sortable(sortable));
      return QuerySetting.this;
    }

  }

}
