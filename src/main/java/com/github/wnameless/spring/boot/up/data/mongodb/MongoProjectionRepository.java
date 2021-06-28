/*
 *
 * Copyright 2021 Wei-Ming Wu
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
package com.github.wnameless.spring.boot.up.data.mongodb;

import static com.github.wnameless.spring.boot.up.data.mongodb.MongoUtils.findDotPaths;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;

public interface MongoProjectionRepository<E>
    extends QuerydslPredicateMongoQueryExecutor<E> {

  default Optional<E> findProjectedBy(Predicate predicate, Class<E> entityType,
      Path<?>... paths) {
    return findProjectedBy(predicate, entityType, findDotPaths(paths));
  }

  default Optional<E> findProjectedBy(Predicate predicate, Class<E> entityType,
      Class<?> projection) {
    return findProjectedBy(predicate, entityType, findDotPaths(projection));
  }

  default Optional<E> findProjectedBy(Predicate predicate, Class<E> entityType,
      String... dotPaths) {
    E target = findOne(predicate, entityType, q -> {
      q.fields().include(dotPaths);
      return q;
    });

    return Optional.ofNullable(target);
  }

  default List<E> findAllProjectedBy(Class<E> entityType, Path<?>... paths) {
    return findAllProjectedBy(entityType, findDotPaths(paths));
  }

  default List<E> findAllProjectedBy(Class<E> entityType, Class<?> projection) {
    return findAllProjectedBy(entityType, findDotPaths(projection));
  }

  default List<E> findAllProjectedBy(Class<E> entityType, String... dotPaths) {
    return findAll(new Query(), entityType, q -> {
      q.fields().include(dotPaths);
      return q;
    });
  }

  default List<E> findAllProjectedBy(Predicate predicate, Class<E> entityType,
      Path<?>... paths) {
    return findAllProjectedBy(predicate, entityType, findDotPaths(paths));
  }

  default List<E> findAllProjectedBy(Predicate predicate, Class<E> entityType,
      Class<?> projection) {
    return findAllProjectedBy(predicate, entityType, findDotPaths(projection));
  }

  default List<E> findAllProjectedBy(Predicate predicate, Class<E> entityType,
      String... dotPaths) {
    return findAll(predicate, entityType, q -> {
      q.fields().include(dotPaths);
      return q;
    });
  }

  default List<E> findAllProjectedBy(Sort sort, Class<E> entityType,
      Path<?>... paths) {
    return findAllProjectedBy(sort, entityType, findDotPaths(paths));
  }

  default List<E> findAllProjectedBy(Sort sort, Class<E> entityType,
      Class<?> projection) {
    return findAllProjectedBy(sort, entityType, findDotPaths(projection));
  }

  default List<E> findAllProjectedBy(Sort sort, Class<E> entityType,
      String... dotPaths) {
    Query query = new Query();
    query.with(sort);

    return findAll(query, entityType, q -> {
      q.fields().include(dotPaths);
      return q;
    });
  }

  default List<E> findAllProjectedBy(Predicate predicate, Sort sort,
      Class<E> entityType, Path<?>... paths) {
    return findAllProjectedBy(predicate, sort, entityType, findDotPaths(paths));
  }

  default List<E> findAllProjectedBy(Predicate predicate, Sort sort,
      Class<E> entityType, Class<?> projection) {
    return findAllProjectedBy(predicate, sort, entityType,
        findDotPaths(projection));
  }

  default List<E> findAllProjectedBy(Predicate predicate, Sort sort,
      Class<E> entityType, String... dotPaths) {
    return findAll(predicate, entityType, q -> {
      q.fields().include(dotPaths);
      q.with(sort);
      return q;
    });
  }

  default Page<E> findPagedProjectedBy(Pageable pageable, Class<E> entityType,
      Path<?>... paths) {
    return findPagedProjectedBy(pageable, entityType, findDotPaths(paths));
  }

  default Page<E> findPagedProjectedBy(Pageable pageable, Class<E> entityType,
      Class<?> projection) {
    return findPagedProjectedBy(pageable, entityType, findDotPaths(projection));
  }

  default Page<E> findPagedProjectedBy(Pageable pageable, Class<E> entityType,
      String... dotPaths) {
    Query query = new Query();
    query.with(pageable);

    List<E> targets = findAll(query, entityType, q -> {
      q.fields().include(dotPaths);
      return q;
    });
    long count = countAll(query, entityType);

    return new PageImpl<>(targets, pageable, count);
  }

  default Page<E> findPagedProjectedBy(Predicate predicate, Pageable pageable,
      Class<E> entityType, Path<?>... paths) {
    return findPagedProjectedBy(predicate, pageable, entityType,
        findDotPaths(paths));
  }

  default Page<E> findPagedProjectedBy(Predicate predicate, Pageable pageable,
      Class<E> entityType, Class<?> projection) {
    return findPagedProjectedBy(predicate, pageable, entityType,
        findDotPaths(projection));
  }

  default Page<E> findPagedProjectedBy(Predicate predicate, Pageable pageable,
      Class<E> entityType, String... dotPaths) {
    List<E> targets = findAll(predicate, entityType, q -> {
      q.fields().include(dotPaths);
      q.with(pageable);
      return q;
    });
    long count = countAll(predicate, entityType, q -> q.with(pageable));

    return new PageImpl<>(targets, pageable, count);
  }

}
