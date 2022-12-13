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
import java.util.function.Function;
import org.bson.Document;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import com.github.wnameless.spring.boot.up.ApplicationContextProvider;
import com.querydsl.core.types.Predicate;

public interface QuerydslPredicateMongoQueryExecutor<E> {

  default MongoOperations getMongoOperations() {
    ApplicationContext appCtx = ApplicationContextProvider.getApplicationContext();
    return appCtx.getBean(MongoOperations.class);
  }

  default E findOne(Query query, Class<E> entityClass) {
    MongoOperations mongoOperations = getMongoOperations();
    return mongoOperations.findOne(query, entityClass);
  }

  default E findOne(Query query, Class<E> entityClass, Function<Query, Query> queryConfig) {
    MongoOperations mongoOperations = getMongoOperations();

    query = queryConfig.apply(query);

    return mongoOperations.findOne(query, entityClass);
  }

  default E findOne(Predicate predicate, Class<E> entityClass) {
    return findOne(predicate, entityClass, q -> q);
  }

  default E findOne(Predicate predicate, Class<E> entityClass, Function<Query, Query> queryConfig) {
    MongoOperations mongoOperations = getMongoOperations();

    Document document =
        new CustomSpringDataMongodbQuery<>(mongoOperations, entityClass).createQuery(predicate);
    Query query = Query.query(new DocumentCriteria(document));
    query = queryConfig.apply(query);

    return mongoOperations.findOne(query, entityClass);
  }

  default List<E> findAll(Query query, Class<E> entityClass) {
    MongoOperations mongoOperations = getMongoOperations();
    return mongoOperations.find(query, entityClass);
  }

  default List<E> findAll(Query query, Class<E> entityClass, Function<Query, Query> queryConfig) {
    MongoOperations mongoOperations = getMongoOperations();

    query = queryConfig.apply(query);

    return mongoOperations.find(query, entityClass);
  }

  default List<E> findAll(Predicate predicate, Class<E> entityClass) {
    return findAll(predicate, entityClass, q -> q);
  }

  default List<E> findAll(Predicate predicate, Class<E> entityClass,
      Function<Query, Query> queryConfig) {
    MongoOperations mongoOperations = getMongoOperations();

    Document document =
        new CustomSpringDataMongodbQuery<>(mongoOperations, entityClass).createQuery(predicate);
    Query query = Query.query(new DocumentCriteria(document));
    query = queryConfig.apply(query);

    return mongoOperations.find(query, entityClass);
  }

  default long countAll(Query query, Class<E> entityClass) {
    MongoOperations mongoOperations = getMongoOperations();
    return mongoOperations.count(query, entityClass);
  }

  default long countAll(Query query, Class<E> entityClass, Function<Query, Query> queryConfig) {
    MongoOperations mongoOperations = getMongoOperations();

    query = queryConfig.apply(query);

    return mongoOperations.count(query, entityClass);
  }

  default long countAll(Predicate predicate, Class<E> entityClass) {
    return countAll(predicate, entityClass, q -> q);
  }

  default long countAll(Predicate predicate, Class<E> entityClass,
      Function<Query, Query> queryConfig) {
    MongoOperations mongoOperations = getMongoOperations();

    Document document =
        new CustomSpringDataMongodbQuery<>(mongoOperations, entityClass).createQuery(predicate);
    Query query = Query.query(new DocumentCriteria(document));
    query = queryConfig.apply(query);

    return mongoOperations.count(query, entityClass);
  }

}
