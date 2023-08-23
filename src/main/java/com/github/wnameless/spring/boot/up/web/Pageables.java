package com.github.wnameless.spring.boot.up.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.web.util.UriComponentsBuilder;

public final class Pageables {

  private Pageables() {}

  private static final Pageables INSTANCE = new Pageables();

  public static Pageables getInstance() {
    return INSTANCE;
  }

  public static String toQueryString(Pageable pageable, PageableParams pageableParams) {
    if (pageable == null) return "";

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/");
    uriBuilder.queryParam(pageableParams.getPageParameter(),
        String.valueOf(pageable.getPageNumber()));
    uriBuilder.queryParam(pageableParams.getSizeParameter(),
        String.valueOf(pageable.getPageSize()));
    pageable.getSort().forEach(order -> {
      uriBuilder.queryParam(pageableParams.getSortParameter(),
          order.getProperty() + "," + order.getDirection());
    });

    return uriBuilder.build().getQuery();
  }

  public static String toQueryString(Pageable pageable) {
    return toQueryString(pageable, PageableParams.ofSpring());
  }

  public static String toQueryStringWithoutPage(Pageable pageable, PageableParams pageableParams) {
    if (pageable == null) return "";

    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/");
    uriBuilder.queryParam(pageableParams.getSizeParameter(),
        String.valueOf(pageable.getPageSize()));
    pageable.getSort().forEach(order -> {
      uriBuilder.queryParam(pageableParams.getSortParameter(),
          order.getProperty() + "," + order.getDirection());
    });

    return uriBuilder.build().getQuery();
  }

  public static String toQueryStringWithoutPage(Pageable pageable) {
    return toQueryStringWithoutPage(pageable, PageableParams.ofSpring());
  }

  public static List<String> sortToParam(Sort sort) {
    return sort.stream().map(o -> o.getProperty() + "," + o.getDirection()).toList();
  }

  public static Sort paramToSort(String... params) {
    return paramToSort(Arrays.asList(params));
  }

  public static Sort paramToSort(List<String> params) {
    if (params == null || params.size() == 0) return Sort.unsorted();

    List<Order> orderList = new ArrayList<>();

    for (String param : params) {
      if (param.trim().isEmpty()) continue;

      String[] propAndDirect = param.split(",");
      if (propAndDirect.length == 1) {
        orderList.add(Order.by(propAndDirect[0]));
      } else if (propAndDirect.length == 2) {
        Direction direction;
        try {
          direction = Direction.fromString(propAndDirect[1]);
          if (direction == Direction.ASC) {
            orderList.add(Order.asc(propAndDirect[0]));
          }
          if (direction == Direction.DESC) {
            orderList.add(Order.desc(propAndDirect[0]));
          }
        } catch (Exception e) {
          orderList.add(Order.by(propAndDirect[0]));
        }
      }
    }

    return Sort.by(orderList);
  }

  public static class PageableParams {

    public static PageableParams of(String pageParameter, String sizeParameter,
        String sortParameter) {
      return new PageableParams(pageParameter, sizeParameter, sortParameter);
    }

    public static PageableParams ofSpring() {
      return new PageableParams("page", "size", "sort");
    }

    private String pageParameter;
    private String sizeParameter;
    private String sortParameter;

    private PageableParams(String pageParameter, String sizeParameter, String sortParameter) {
      this.pageParameter = pageParameter;
      this.sizeParameter = sizeParameter;
      this.sortParameter = sortParameter;
    }

    public String getPageParameter() {
      return pageParameter;
    }

    public String getSizeParameter() {
      return sizeParameter;
    }

    public String getSortParameter() {
      return sortParameter;
    }

  }

}
