/*
 *
 * Copyright 2020 Wei-Ming Wu
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
package com.github.wnameless.spring.boot.up.permission.resource;

import static com.github.wnameless.spring.boot.up.data.mongodb.MongoUtils.findDotPaths;

import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.github.wnameless.spring.boot.up.ApplicationContextProvider;
import com.github.wnameless.spring.boot.up.data.mongodb.MongoProjectionRepository;
import com.github.wnameless.spring.boot.up.permission.PermittedUser;
import com.github.wnameless.spring.boot.up.permission.WebPermissionManager;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;

@NoRepositoryBean
public interface ResourceFilterRepository<T, ID> extends CrudRepository<T, ID>,
    QuerydslPredicateExecutor<T>, MongoProjectionRepository<T> {

  @SuppressWarnings("rawtypes")
  default ResourceAccessRule getResourceAccessRule() {
    ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
    WebPermissionManager wpm = ctx.getBean(WebPermissionManager.class);
    ResourceAccessRule rar =
        wpm.findUserResourceAccessRuleByRepositoryType(this.getClass());
    return rar;
  }

  @SuppressWarnings("rawtypes")
  default PermittedUser getCurrentUser() {
    ApplicationContext ctx = ApplicationContextProvider.getApplicationContext();
    PermittedUser user = ctx.getBean(PermittedUser.class);
    return user;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Optional<T> filterFindOne(Predicate predicate) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findOne(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate));
    }
    return findOne(
        ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Iterable<T> filterFindAll() {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(rar.getPredicateOfManageAbility());
    }
    return findAll(rar.getPredicateOfReadAbility());
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Iterable<T> filterFindAll(Predicate predicate) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate));
    }
    return findAll(
        ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Iterable<T> filterFindAll(Sort sort) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(rar.getPredicateOfManageAbility(), sort);
    }
    return findAll(rar.getPredicateOfReadAbility(), sort);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Iterable<T> filterFindAll(Predicate predicate, Sort sort) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate),
          sort);
    }
    return findAll(
        ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate),
        sort);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Iterable<T> filterFindAll(OrderSpecifier<?>... orders) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(ExpressionUtils.allOf(rar.getPredicateOfManageAbility()),
          orders);
    }
    return findAll(ExpressionUtils.allOf(rar.getPredicateOfReadAbility()),
        orders);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Iterable<T> filterFindAll(Predicate predicate,
      OrderSpecifier<?>... orders) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate),
          orders);
    }
    return findAll(
        ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate),
        orders);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Page<T> filterFindAll(Pageable pageable) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(ExpressionUtils.allOf(rar.getPredicateOfManageAbility()),
          pageable);
    }
    return findAll(ExpressionUtils.allOf(rar.getPredicateOfReadAbility()),
        pageable);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Page<T> filterFindAll(Predicate predicate, Pageable pageable) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate),
          pageable);
    }
    return findAll(
        ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate),
        pageable);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default long filterCount() {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return count(rar.getPredicateOfManageAbility());
    }
    return count(rar.getPredicateOfReadAbility());
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default long filterCount(Predicate predicate) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return count(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate));
    }
    return count(
        ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default boolean filterExists(Predicate predicate) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return exists(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate));
    }
    return exists(
        ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default T filterSave(T entity) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    Predicate idEq = rar.getPredicateOfEntity(entity);
    Optional<T> target = findOne(idEq);
    // new entity
    if (!target.isPresent()) {
      if (!user.canCreate(rar.getResourceType())) {
        throw new UnsupportedOperationException("No permission to CREATE");
      }

      return save(entity);
    }

    // check if entity existed and accessible
    if (user.canManage(rar.getResourceType())) {
      target = findOne(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), idEq));
    } else {
      target = findOne(
          ExpressionUtils.allOf(rar.getPredicateOfUpdateAbility(), idEq));
    }
    if (!target.isPresent()) {
      throw new UnsupportedOperationException("No permission to UPDATE");
    }

    return save(entity);
  }

  default Optional<T> filterFindById(ID id) {
    @SuppressWarnings("unchecked")
    Predicate idEq = getResourceAccessRule().getPredicateOfEntityId(id);
    return filterFindOne(idEq);
  }

  default boolean filterExistsById(ID id) {
    @SuppressWarnings("unchecked")
    Predicate idEq = getResourceAccessRule().getPredicateOfEntityId(id);
    return filterExists(idEq);
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default void filterDeleteById(ID id) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canDestroy(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to DESTROY");
    }

    Predicate idEq = rar.getPredicateOfEntityId(id);
    Optional<T> target;
    if (user.canManage(rar.getResourceType())) {
      target = findOne(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), idEq));
    } else {
      target = findOne(
          ExpressionUtils.allOf(rar.getPredicateOfDestroyAbility(), idEq));
    }

    if (target.isPresent()) {
      delete(target.get());
    }
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default void filterDelete(T entity) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canDestroy(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to DESTROY");
    }

    Predicate idEq = rar.getPredicateOfEntity(entity);
    Optional<T> target;
    if (user.canManage(rar.getResourceType())) {
      target = findOne(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), idEq));
    } else {
      target = findOne(
          ExpressionUtils.allOf(rar.getPredicateOfDestroyAbility(), idEq));
    }

    if (target.isPresent()) {
      delete(target.get());
    }
  }

  // Projection APIs

  default Optional<T> filterFindProjectedBy(Predicate predicate,
      Path<?>... paths) {
    return filterFindProjectedBy(predicate, findDotPaths(paths));
  }

  default Optional<T> filterFindProjectedBy(Predicate predicate,
      Class<?> projection) {
    return filterFindProjectedBy(predicate, findDotPaths(projection));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Optional<T> filterFindProjectedBy(Predicate predicate,
      String... dotPaths) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return this.findProjectedBy(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate),
          dotPaths);
    }
    return findProjectedBy(
        ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate),
        dotPaths);
  }

  default List<T> filterFindAllProjectedBy(Path<?>... paths) {
    return filterFindAllProjectedBy(findDotPaths(paths));
  }

  default List<T> filterFindAllProjectedBy(Class<?> projection) {
    return filterFindAllProjectedBy(findDotPaths(projection));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default List<T> filterFindAllProjectedBy(String... dotPaths) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAllProjectedBy(rar.getPredicateOfManageAbility(), dotPaths);
    }
    return findAllProjectedBy(rar.getPredicateOfReadAbility(), dotPaths);
  }

  default List<T> filterFindAllProjectedBy(Predicate predicate,
      Path<?>... paths) {
    return filterFindAllProjectedBy(predicate, findDotPaths(paths));
  }

  default List<T> filterFindAllProjectedBy(Predicate predicate,
      Class<?> projection) {
    return filterFindAllProjectedBy(predicate, findDotPaths(projection));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default List<T> filterFindAllProjectedBy(Predicate predicate,
      String... dotPaths) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAllProjectedBy(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate),
          dotPaths);
    }
    return findAllProjectedBy(
        ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate),
        dotPaths);
  }

  default List<T> filterFindAllProjectedBy(Sort sort, Path<?>... paths) {
    return filterFindAllProjectedBy(sort, findDotPaths(paths));
  }

  default List<T> filterFindAllProjectedBy(Sort sort, Class<?> projection) {
    return filterFindAllProjectedBy(sort, findDotPaths(projection));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default List<T> filterFindAllProjectedBy(Sort sort, String... dotPaths) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAllProjectedBy(rar.getPredicateOfManageAbility(), sort,
          dotPaths);
    }
    return findAllProjectedBy(rar.getPredicateOfReadAbility(), sort, dotPaths);
  }

  default List<T> filterFindAllProjectedBy(Predicate predicate, Sort sort,
      Path<?>... paths) {
    return filterFindAllProjectedBy(predicate, sort, findDotPaths(paths));
  }

  default List<T> filterFindAllProjectedBy(Predicate predicate, Sort sort,
      Class<?> projection) {
    return filterFindAllProjectedBy(predicate, sort, findDotPaths(projection));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default List<T> filterFindAllProjectedBy(Predicate predicate, Sort sort,
      String... dotPaths) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAllProjectedBy(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate),
          sort, dotPaths);
    }
    return findAllProjectedBy(
        ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate), sort,
        dotPaths);
  }

  default Page<T> filterFindPagedProjectedBy(Pageable pageable,
      Path<?>... paths) {
    return filterFindPagedProjectedBy(pageable, findDotPaths(paths));
  }

  default Page<T> filterFindPagedProjectedBy(Pageable pageable,
      Class<?> projection) {
    return filterFindPagedProjectedBy(pageable, findDotPaths(projection));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Page<T> filterFindPagedProjectedBy(Pageable pageable,
      String... dotPaths) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findPagedProjectedBy(rar.getPredicateOfManageAbility(), pageable,
          dotPaths);
    }
    return findPagedProjectedBy(rar.getPredicateOfReadAbility(), pageable,
        dotPaths);
  }

  default Page<T> filterFindPagedProjectedBy(Predicate predicate,
      Pageable pageable, Path<?>... paths) {
    return filterFindPagedProjectedBy(predicate, pageable, findDotPaths(paths));
  }

  default Page<T> filterFindPagedProjectedBy(Predicate predicate,
      Pageable pageable, Class<?> projection) {
    return filterFindPagedProjectedBy(predicate, pageable,
        findDotPaths(projection));
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  default Page<T> filterFindPagedProjectedBy(Predicate predicate,
      Pageable pageable, String... dotPaths) {
    ResourceAccessRule rar = getResourceAccessRule();
    PermittedUser user = getCurrentUser();
    if (!user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findPagedProjectedBy(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate),
          pageable, dotPaths);
    }
    return findPagedProjectedBy(
        ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate),
        pageable, dotPaths);
  }

}
