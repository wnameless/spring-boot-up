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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.NoRepositoryBean;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.base.JacksonJsonValue;
import com.github.wnameless.json.flattener.FlattenMode;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.querydsl.core.types.Predicate;

@NoRepositoryBean
public interface MongoProjectionRepository<E> extends PredicateInterchangeableQueryRepository<E> {

  Logger log = LoggerFactory.getLogger(MongoProjectionRepository.class);

  default Set<String> findTypeFieldNames(Class<?> klass) {
    try {
      Object obj = klass.newInstance();
      ObjectMapper mapper = new ObjectMapper();
      JsonNode jsonNode = mapper.valueToTree(obj);

      JsonFlattener jf = new JsonFlattener(new JacksonJsonValue(jsonNode));
      Map<String, Object> flattenedMap =
          jf.withFlattenMode(FlattenMode.KEEP_ARRAYS).flattenAsMap();

      log.debug(flattenedMap.keySet().toString());
      return flattenedMap.keySet();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  default Optional<E> findProjectedBy(Predicate predicate, Class<E> entityType,
      Class<?> projection) {
    Set<String> fieldNames = findTypeFieldNames(projection);

    E target = findOne(predicate, entityType, q -> {
      q.fields().include(fieldNames.stream().toArray(String[]::new));
      return q;
    });

    return Optional.ofNullable(target);
  }

  default List<E> findAllProjectedBy(Class<E> entityType, Class<?> projection) {
    Set<String> fieldNames = findTypeFieldNames(projection);

    return findAll(new Query(), entityType, q -> {
      q.fields().include(fieldNames.stream().toArray(String[]::new));
      return q;
    });
  }

  default List<E> findAllProjectedBy(Predicate predicate, Class<E> entityType,
      Class<?> projection) {
    Set<String> fieldNames = findTypeFieldNames(projection);

    return findAll(predicate, entityType, q -> {
      q.fields().include(fieldNames.stream().toArray(String[]::new));
      return q;
    });
  }

  default List<E> findAllProjectedBy(Class<E> entityType, Sort sort,
      Class<?> projection) {
    Set<String> fieldNames = findTypeFieldNames(projection);

    Query query = new Query();
    query.with(sort);

    return findAll(query, entityType, q -> {
      q.fields().include(fieldNames.stream().toArray(String[]::new));
      return q;
    });
  }

  default List<E> findAllProjectedBy(Predicate predicate, Sort sort,
      Class<E> entityType, Class<?> projection) {
    Set<String> fieldNames = findTypeFieldNames(projection);

    return findAll(predicate, entityType, q -> {
      q.fields().include(fieldNames.stream().toArray(String[]::new));
      q.with(sort);
      return q;
    });
  }

  default Page<E> findPagedProjectedBy(Pageable pageable, Class<E> entityType,
      Class<?> projection) {
    Set<String> fieldNames = findTypeFieldNames(projection);

    Query query = new Query();
    query.with(pageable);

    List<E> targets = findAll(query, entityType, q -> {
      q.fields().include(fieldNames.stream().toArray(String[]::new));
      return q;
    });
    long count = countAll(query, entityType);

    return new PageImpl<>(targets, pageable, count);
  }

  default Page<E> findPagedProjectedBy(Predicate predicate, Pageable pageable,
      Class<E> entityType, Class<?> projection) {
    Set<String> fieldNames = findTypeFieldNames(projection);

    List<E> targets = findAll(predicate, entityType, q -> {
      q.fields().include(fieldNames.stream().toArray(String[]::new));
      q.with(pageable);
      return q;
    });
    long count = countAll(predicate, entityType, q -> q.with(pageable));

    return new PageImpl<>(targets, pageable, count);
  }

}
