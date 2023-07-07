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
  private PageableParams pageableParams;
  private final MultiValueMap<String, String> requestParams;

  public QueryConfig(QuerySetting<E> querySetting, MultiValueMap<String, String> requestParams) {
    this.entity = querySetting.getEntity();
    this.filterFields = querySetting.getFilterFields();
    this.pageableParams = querySetting.getPageableParams();
    this.requestParams = querySetting.defaultParams(requestParams);
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

  public String getFieldQueryString(String field) {
    Map<String, String> filters = new LinkedHashMap<>();
    for (Entry<String, FilterableField<E>> entry : filterFields.entrySet()) {
      String f = entry.getKey();
      filters.put(f, requestParams.getFirst(f));
    }

    return filters.get(field);
  }

  public String getFieldSortName(String field) {
    return filterFields.get(field).getSortableFieldName(entity);
  }

  @SneakyThrows
  public String toQueryString(boolean excludePage, boolean excludeSort) {
    StringBuilder queryStr = new StringBuilder("?");

    if (!excludePage) {
      String pageKey = pageableParams.getPageParameter();
      if (requestParams.containsKey(pageKey)) {
        queryStr.append(URLEncoder.encode(pageKey, "UTF-8"));
        queryStr.append('=');
        queryStr.append(getPage());
        queryStr.append('&');
      }
    }

    String sizeKey = pageableParams.getSizeParameter();
    if (requestParams.containsKey(sizeKey)) {
      queryStr.append(URLEncoder.encode(sizeKey, "UTF-8"));
      queryStr.append('=');
      queryStr.append(getSize());
      queryStr.append('&');
    }

    if (!excludeSort) {
      String sortKey = pageableParams.getSortParameter();
      for (Order order : getSort()) {
        String property = order.getProperty();
        String direction = order.getDirection().toString();

        queryStr.append(URLEncoder.encode(sortKey, "UTF-8"));
        queryStr.append('=');
        queryStr.append(URLEncoder.encode(property, "UTF-8"));
        queryStr.append(',');
        queryStr.append(direction);
        queryStr.append('&');
      }
    }

    for (Entry<String, FilterableField<E>> entry : filterFields.entrySet()) {
      String field = entry.getKey();
      if (!requestParams.containsKey(field)) {
        continue;
      }

      queryStr.append(URLEncoder.encode(field, "UTF-8"));
      queryStr.append('=');
      queryStr.append(URLEncoder.encode(requestParams.getFirst(field), "UTF-8"));
      queryStr.append('&');
    }
    return queryStr.toString();
  }

  @SneakyThrows
  public String toQueryString() {
    return toQueryString(false, false);
  }

  @SneakyThrows
  public String toQueryStringWithoutSort() {
    StringBuilder queryStr = new StringBuilder("?");

    String sizeKey = pageableParams.getSizeParameter();
    if (requestParams.containsKey(sizeKey)) {
      queryStr.append(URLEncoder.encode(sizeKey, "UTF-8"));
      queryStr.append('=');
      queryStr.append(getSize());
      queryStr.append('&');
    }

    for (Entry<String, FilterableField<E>> entry : filterFields.entrySet()) {
      String field = entry.getKey();
      if (!requestParams.containsKey(field)) {
        continue;
      }

      queryStr.append(URLEncoder.encode(field, "UTF-8"));
      queryStr.append('=');
      queryStr.append(URLEncoder.encode(requestParams.getFirst(field), "UTF-8"));
      queryStr.append('&');
    }
    return queryStr.toString();
  }

  public Predicate getPredicate() {
    Predicate predicate = null;

    for (String field : getFields()) {
      if (requestParams.containsKey(field)) {
        String param = requestParams.getFirst(field);
        if (param == null || param.isEmpty()) continue;

        FilterableField<E> ff = filterFields.get(field);
        if (predicate == null) {
          predicate = ff.getFilterLogic(entity).apply(requestParams.getFirst(field));
        } else {
          predicate = ExpressionUtils.allOf(predicate,
              ff.getFilterLogic(entity).apply(requestParams.getFirst(field)));
        }

      }
    }

    return predicate == null ? new BooleanBuilder() : predicate;
  }

  private int getPage() {
    if (requestParams.containsKey(pageableParams.getPageParameter())) {
      return Integer.parseInt(requestParams.getFirst(pageableParams.getPageParameter()));
    }
    return 0;
  }

  private int getSize() {
    if (requestParams.containsKey(pageableParams.getSizeParameter())) {
      return Integer.parseInt(requestParams.getFirst(pageableParams.getSizeParameter()));
    }
    return 10;
  }

  private Sort getSort() {
    if (requestParams.containsKey(pageableParams.getSortParameter())) {
      return Pageables.paramToSort(requestParams.get(pageableParams.getSortParameter()));
    }
    return Sort.unsorted();
  }

  public Pageable getPageable() {
    return PageRequest.of(getPage(), getSize(), getSort());
  }

}
