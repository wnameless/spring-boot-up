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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanOperation;

import lombok.Data;
import lombok.SneakyThrows;
import net.sf.rubycollect4j.Ruby;

@Data
public final class QueryConfig {

  private final Pageable pageable;
  private final Predicate predicate;
  private final List<String> fields;

  public QueryConfig(Pageable pageable, Predicate predicate, String... fields) {
    this.pageable = pageable;
    this.predicate = predicate;
    this.fields = new ArrayList<>(Arrays.asList(fields));
  }

  public Map<String, Order> getSorts() {
    Map<String, Order> sorts = new LinkedHashMap<>();
    pageable.getSort().forEach(order -> sorts.put(order.getProperty(), order));
    return sorts;
  }

  public Map<String, String> getFilters() {
    Map<String, String> filters = new LinkedHashMap<>();
    if (predicate instanceof BooleanOperation) {
      BooleanOperation bo = (BooleanOperation) predicate;
      for (var ra : Ruby.Array.of(bo.getArgs()).eachSlice(2)) {
        filters.put(ra.get(0).toString().split(Pattern.quote("."))[1], ra.get(1).toString());
      }
    }
    return filters;
  }

  @SneakyThrows
  public String toQueryString() {
    StringBuilder queryStr = new StringBuilder("?");

    if (pageable != null) {
      int size = pageable.getPageSize();
      queryStr.append("size");
      queryStr.append('=');
      queryStr.append(size);
      queryStr.append('&');

      for (Order order : pageable.getSort()) {
        String name = order.getProperty();
        String direction = order.getDirection().toString();

        queryStr.append("sort");
        queryStr.append('=');
        queryStr.append(URLEncoder.encode(name, "UTF-8"));
        queryStr.append(',');
        queryStr.append(direction);
        queryStr.append('&');
      }
    }

    if (predicate instanceof BooleanOperation) {
      BooleanOperation bo = (BooleanOperation) predicate;
      for (var ra : Ruby.Array.of(bo.getArgs()).eachSlice(2)) {
        queryStr.append(URLEncoder.encode(ra.get(0).toString().split(Pattern.quote("."))[1], "UTF-8"));
        queryStr.append('=');
        queryStr.append(ra.get(1).toString());
        queryStr.append('&');
      }
    }

    return queryStr.toString();
  }

}
