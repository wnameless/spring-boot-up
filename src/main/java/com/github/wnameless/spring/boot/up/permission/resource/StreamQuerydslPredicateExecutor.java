package com.github.wnameless.spring.boot.up.permission.resource;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;

public interface StreamQuerydslPredicateExecutor<T> extends QuerydslPredicateExecutor<T> {

  default Stream<T> findAllOnStream(Predicate predicate) {
    return StreamSupport.stream(findAll(predicate).spliterator(), false);
  }

  default Stream<T> findAllOnStream(Predicate predicate, Sort sort) {
    return StreamSupport.stream(findAll(predicate, sort).spliterator(), false);
  }

  default Stream<T> findAllOnStream(Predicate predicate, OrderSpecifier<?>... orders) {
    return StreamSupport.stream(findAll(predicate, orders).spliterator(), false);
  }

  default Stream<T> findAllOnStream(OrderSpecifier<?>... orders) {
    return StreamSupport.stream(findAll(orders).spliterator(), false);
  }

}
