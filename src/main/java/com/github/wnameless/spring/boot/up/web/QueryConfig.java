package com.github.wnameless.spring.boot.up.web;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.MultiValueMap;

import com.github.wnameless.spring.boot.up.web.Pageables.PageableParams;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;

import lombok.Data;
import lombok.SneakyThrows;

@Data
public final class QueryConfig<E extends EntityPathBase<?>> {

  private final E entity;
  private final Map<String, FilterableField<E>> filterFields;
  private final PageableParams pageableParams;
  private final MultiValueMap<String, String> requestParams;

  public QueryConfig(QuerySetting<E> queryConfig, MultiValueMap<String, String> requestParams) {
    this.entity = queryConfig.getEntity();
    this.filterFields = queryConfig.getFilterFields();
    this.pageableParams = queryConfig.getPageableParams();
    this.requestParams = requestParams;
  }

  public List<String> getFields() {
    return new ArrayList<>(filterFields.keySet());
  }

  public Order getFieldOrder(String field) {
    Map<String, Order> sorts = new LinkedHashMap<>();
    if (requestParams.containsKey(pageableParams.getSortParameter())) {
      Sort sort = Pageables.paramToSort(requestParams.get(pageableParams.getSortParameter()));
      sort.forEach(order -> sorts.put(order.getProperty(), order));
    }

    return sorts.get(field);
  }

  public String getFieldQuery(String field) {
    Map<String, String> filters = new LinkedHashMap<>();

    for (Entry<String, FilterableField<E>> entry : filterFields.entrySet()) {
      String f = entry.getKey();
      filters.put(f, requestParams.getFirst(f));
    }

    return filters.get(field);
  }

  @SneakyThrows
  public String toQueryString() {
    StringBuilder queryStr = new StringBuilder("?");

    // if (pageable != null) {
    if (requestParams.containsKey(pageableParams.getSizeParameter())) {
      // int size = pageable.getPageSize();
      queryStr.append("size");
      queryStr.append('=');
      queryStr.append(getSize());
      queryStr.append('&');
    }
    for (Order order : getSort()) {
      String name = order.getProperty();
      String direction = order.getDirection().toString();

      queryStr.append("sort");
      queryStr.append('=');
      queryStr.append(URLEncoder.encode(name, "UTF-8"));
      queryStr.append(',');
      queryStr.append(direction);
      queryStr.append('&');
    }

    for (Entry<String, FilterableField<E>> entry : filterFields.entrySet()) {
      String field = entry.getKey();
      queryStr.append(URLEncoder.encode(field, "UTF-8"));
      queryStr.append('=');
      queryStr.append(requestParams.get(field));
      queryStr.append('&');
    }
    return queryStr.toString();
  }

  public Predicate getPredicate() {
    Predicate predicate = null;

    for (String field : getFields()) {
      if (requestParams.containsKey(field)) {
        FilterableField<E> ff = filterFields.get(field);
        if (predicate == null) {
          predicate = ff.getLogic(entity).apply(requestParams.getFirst(field));
        } else {
          predicate = ExpressionUtils.allOf(predicate,
              ff.getLogic(entity).apply(requestParams.getFirst(field)));
        }

      }
    }

    return predicate == null ? new BooleanBuilder() : predicate;
  }

  public int getPage() {
    if (requestParams.containsKey(pageableParams.getPageParameter())) {
      return Integer.parseInt(requestParams.getFirst(pageableParams.getPageParameter()));
    }
    return 0;
  }

  public int getSize() {
    if (requestParams.containsKey(pageableParams.getSizeParameter())) {
      return Integer.parseInt(requestParams.getFirst(pageableParams.getSizeParameter()));
    }
    return 10;
  }

  public Sort getSort() {
    if (requestParams.containsKey(pageableParams.getSortParameter())) {
      return Pageables.paramToSort(requestParams.get(pageableParams.getSortParameter()));
    }
    return Sort.unsorted();
  }

  public Pageable getPageable() {
    return PageRequest.of(getPage(), getSize(), getSort());
  }

}
