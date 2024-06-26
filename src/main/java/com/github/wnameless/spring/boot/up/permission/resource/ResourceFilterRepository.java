package com.github.wnameless.spring.boot.up.permission.resource;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.http.HttpStatus;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.data.mongodb.querydsl.MongoProjectionRepository;
import com.github.wnameless.spring.boot.up.data.mongodb.querydsl.MongoQuerydslUtils;
import com.github.wnameless.spring.boot.up.permission.PermittedUser;
import com.github.wnameless.spring.boot.up.permission.WebPermissionManager;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Alert;
import com.github.wnameless.spring.boot.up.web.SpringBootUpWeb;
import com.github.wnameless.spring.boot.up.web.WebActionAlertHelper.AlertMessages;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import jakarta.validation.Validator;

@NoRepositoryBean
public interface ResourceFilterRepository<T, ID> extends CrudRepository<T, ID>,
    StreamQuerydslPredicateExecutor<T>, MongoProjectionRepository<T> {

  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ResourceFilterRepository.class);

  default Optional<ResourceAccessRule<?, ?, ?>> findResourceAccessRule() {
    WebPermissionManager wpm = SpringBootUp.getBean(WebPermissionManager.class);
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt =
        wpm.findUserResourceAccessRuleByRepositoryType(this.getClass());
    if (rarOpt.isEmpty()) {
      log.warn("User {} with roles: {} don't have enough permission on {}",
          getCurrentUser().getUsername(), getCurrentUser().getAllRoles(),
          AopProxyUtils.proxiedUserInterfaces(this)[0].getSimpleName());
    }
    return rarOpt;
  }

  @SuppressWarnings("rawtypes")
  default PermittedUser getCurrentUser() {
    PermittedUser user = SpringBootUp.getBean(PermittedUser.class);
    return user;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Optional<T> filterFindOne(Predicate predicate) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findOne(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate));
    }
    return findOne(ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate));
  }

  default Stream<T> filterFindAllOnStream() {
    return StreamSupport.stream(filterFindAll().spliterator(), false);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<T> filterFindAll() {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(rar.getPredicateOfManageAbility());
    }
    return findAll(rar.getPredicateOfReadAbility());
  }

  default Stream<T> filterFindAllOnStream(Predicate predicate) {
    return StreamSupport.stream(filterFindAll(predicate).spliterator(), false);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<T> filterFindAll(Predicate predicate) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate));
    }
    return findAll(ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate));
  }

  default Stream<T> filterFindAllOnStream(Sort sort) {
    return StreamSupport.stream(filterFindAll(sort).spliterator(), false);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<T> filterFindAll(Sort sort) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(rar.getPredicateOfManageAbility(), sort);
    }
    return findAll(rar.getPredicateOfReadAbility(), sort);
  }

  default Stream<T> filterFindAllOnStream(Predicate predicate, Sort sort) {
    return StreamSupport.stream(filterFindAll(predicate, sort).spliterator(), false);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<T> filterFindAll(Predicate predicate, Sort sort) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate), sort);
    }
    return findAll(ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate), sort);
  }

  default Stream<T> filterFindAllOnStream(OrderSpecifier<?>... orders) {
    return StreamSupport.stream(filterFindAll(orders).spliterator(), false);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<T> filterFindAll(OrderSpecifier<?>... orders) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(ExpressionUtils.allOf(rar.getPredicateOfManageAbility()), orders);
    }
    return findAll(ExpressionUtils.allOf(rar.getPredicateOfReadAbility()), orders);
  }

  default Stream<T> filterFindAllOnStream(Predicate predicate, OrderSpecifier<?>... orders) {
    return StreamSupport.stream(filterFindAll(predicate, orders).spliterator(), false);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<T> filterFindAll(Predicate predicate, OrderSpecifier<?>... orders) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate), orders);
    }
    return findAll(ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate), orders);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Page<T> filterFindAll(Pageable pageable) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(ExpressionUtils.allOf(rar.getPredicateOfManageAbility()), pageable);
    }
    return findAll(ExpressionUtils.allOf(rar.getPredicateOfReadAbility()), pageable);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Page<T> filterFindAll(Predicate predicate, Pageable pageable) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAll(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate), pageable);
    }
    return findAll(ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate), pageable);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default long filterCount() {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return count(rar.getPredicateOfManageAbility());
    }
    return count(rar.getPredicateOfReadAbility());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default long filterCount(Predicate predicate) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return count(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate));
    }
    return count(ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean filterExists(Predicate predicate) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return exists(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate));
    }
    return exists(ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default T filterSave(T entity) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    Predicate idEq = rar.getPredicateOfEntity(entity);
    Optional<T> target = findOne(idEq);

    // new entity
    if (target.isEmpty()) {
      if (rarOpt.isEmpty() || !user.canCreate(rar.getResourceType())) {
        throw new UnsupportedOperationException("No permission to CREATE");
      }

      return save(entity);
    }

    // check if entity existed and accessible
    if (user.canManage(rar.getResourceType())) {
      target = findOne(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), idEq));
    } else {
      target = findOne(ExpressionUtils.allOf(rar.getPredicateOfUpdateAbility(), idEq));
    }
    if (target.isEmpty()) {
      throw new UnsupportedOperationException("No permission to UPDATE");
    }

    return save(entity);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default T filterSaveWithValidation(T entity) {
    Validator validator = SpringBootUp.getBean(Validator.class);

    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    Predicate idEq = rar.getPredicateOfEntity(entity);
    Optional<T> target = findOne(idEq);

    // validates bean
    List<String> messages = validator.validate(entity).stream().map(e -> e.getMessage()).toList();
    if (messages.size() > 0) {
      var alertMessages = new AlertMessages();
      alertMessages.setWarning(messages);
      SpringBootUpWeb.findHttpServletRequest().ifPresent(req -> {
        req.setAttribute(Alert.name(), alertMessages);
      });
      SpringBootUpWeb.findHttpServletResponse().ifPresent(rsp -> {
        rsp.setStatus(HttpStatus.PRECONDITION_FAILED.value());
      });
      return entity;
    }

    // new entity
    if (target.isEmpty()) {
      if (rarOpt.isEmpty() || !user.canCreate(rar.getResourceType())) {
        var alertMessages = new AlertMessages();
        alertMessages.getWarning().add("No permission to CREATE");
        SpringBootUpWeb.findHttpServletRequest().ifPresent(req -> {
          req.setAttribute(Alert.name(), alertMessages);
        });
        SpringBootUpWeb.findHttpServletResponse().ifPresent(rsp -> {
          rsp.setStatus(HttpStatus.PRECONDITION_FAILED.value());
        });
        return entity;
      }

      return trySave(entity);
    }

    // checks if entity existed and accessible
    if (user.canManage(rar.getResourceType())) {
      target = findOne(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), idEq));
    } else {
      target = findOne(ExpressionUtils.allOf(rar.getPredicateOfUpdateAbility(), idEq));
    }
    if (target.isEmpty()) {
      var alertMessages = new AlertMessages();
      alertMessages.getWarning().add("No permission to UPDATE");
      SpringBootUpWeb.findHttpServletRequest().ifPresent(req -> {
        req.setAttribute(Alert.name(), alertMessages);
      });
      SpringBootUpWeb.findHttpServletResponse().ifPresent(rsp -> {
        rsp.setStatus(HttpStatus.PRECONDITION_FAILED.value());
      });
      return entity;
    }

    return trySave(entity);
  }

  default T saveWithValidation(T entity) {
    Validator validator = SpringBootUp.getBean(Validator.class);

    // validates bean
    List<String> messages = validator.validate(entity).stream().map(e -> e.getMessage()).toList();
    if (messages.size() > 0) {
      var alertMessages = new AlertMessages();
      alertMessages.setWarning(messages);
      SpringBootUpWeb.findHttpServletRequest().ifPresent(req -> {
        req.setAttribute(Alert.name(), alertMessages);
      });
      SpringBootUpWeb.findHttpServletResponse().ifPresent(rsp -> {
        rsp.setStatus(HttpStatus.PRECONDITION_FAILED.value());
      });
      return entity;
    }

    return trySave(entity);
  }

  default T trySave(T entity) {
    try {
      return save(entity);
    } catch (Exception e) {
      var alertMessages = new AlertMessages();
      alertMessages.getDanger().add(e.getMessage());
      SpringBootUpWeb.findHttpServletRequest().ifPresent(req -> {
        req.setAttribute(Alert.name(), alertMessages);
      });
      SpringBootUpWeb.findHttpServletResponse().ifPresent(rsp -> {
        rsp.setStatus(HttpStatus.FORBIDDEN.value());
      });
      return entity;
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  default Optional<T> filterFindById(ID id) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }
    Predicate idEq = rar.getPredicateOfEntityId(id);
    return filterFindOne(idEq);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  default boolean filterExistsById(ID id) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }
    Predicate idEq = rar.getPredicateOfEntityId(id);
    return filterExists(idEq);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default void filterDeleteById(ID id) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canDelete(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to DESTROY");
    }

    Predicate idEq = rar.getPredicateOfEntityId(id);
    Optional<T> target;
    if (user.canManage(rar.getResourceType())) {
      target = findOne(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), idEq));
    } else {
      target = findOne(ExpressionUtils.allOf(rar.getPredicateOfDeleteAbility(), idEq));
    }

    if (target.isPresent()) {
      delete(target.get());
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default void filterDelete(T entity) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canDelete(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to DELETE");
    }

    Predicate idEq = rar.getPredicateOfEntity(entity);
    Optional<T> target;
    if (user.canManage(rar.getResourceType())) {
      target = findOne(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), idEq));
    } else {
      target = findOne(ExpressionUtils.allOf(rar.getPredicateOfDeleteAbility(), idEq));
    }

    if (target.isPresent()) {
      delete(target.get());
    }
  }

  // Projection APIs

  default Optional<T> filterFindProjectedBy(Predicate predicate, Path<?>... paths) {
    return filterFindProjectedBy(predicate, MongoQuerydslUtils.findDotPaths(paths));
  }

  default Optional<T> filterFindProjectedBy(Predicate predicate, Class<?> projection) {
    return filterFindProjectedBy(predicate, MongoQuerydslUtils.findDotPaths(projection));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Optional<T> filterFindProjectedBy(Predicate predicate, String... dotPaths) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return this.findProjectedBy(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate), dotPaths);
    }
    return findProjectedBy(ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate),
        dotPaths);
  }

  default List<T> filterFindAllProjectedBy(Path<?>... paths) {
    return filterFindAllProjectedBy(MongoQuerydslUtils.findDotPaths(paths));
  }

  default List<T> filterFindAllProjectedBy(Class<?> projection) {
    return filterFindAllProjectedBy(MongoQuerydslUtils.findDotPaths(projection));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default List<T> filterFindAllProjectedBy(String... dotPaths) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAllProjectedBy(rar.getPredicateOfManageAbility(), dotPaths);
    }
    return findAllProjectedBy(rar.getPredicateOfReadAbility(), dotPaths);
  }

  default List<T> filterFindAllProjectedBy(Predicate predicate, Path<?>... paths) {
    return filterFindAllProjectedBy(predicate, MongoQuerydslUtils.findDotPaths(paths));
  }

  default List<T> filterFindAllProjectedBy(Predicate predicate, Class<?> projection) {
    return filterFindAllProjectedBy(predicate, MongoQuerydslUtils.findDotPaths(projection));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default List<T> filterFindAllProjectedBy(Predicate predicate, String... dotPaths) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAllProjectedBy(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate),
          dotPaths);
    }
    return findAllProjectedBy(ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate),
        dotPaths);
  }

  default List<T> filterFindAllProjectedBy(Sort sort, Path<?>... paths) {
    return filterFindAllProjectedBy(sort, MongoQuerydslUtils.findDotPaths(paths));
  }

  default List<T> filterFindAllProjectedBy(Sort sort, Class<?> projection) {
    return filterFindAllProjectedBy(sort, MongoQuerydslUtils.findDotPaths(projection));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default List<T> filterFindAllProjectedBy(Sort sort, String... dotPaths) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAllProjectedBy(rar.getPredicateOfManageAbility(), sort, dotPaths);
    }
    return findAllProjectedBy(rar.getPredicateOfReadAbility(), sort, dotPaths);
  }

  default List<T> filterFindAllProjectedBy(Predicate predicate, Sort sort, Path<?>... paths) {
    return filterFindAllProjectedBy(predicate, sort, MongoQuerydslUtils.findDotPaths(paths));
  }

  default List<T> filterFindAllProjectedBy(Predicate predicate, Sort sort, Class<?> projection) {
    return filterFindAllProjectedBy(predicate, sort, MongoQuerydslUtils.findDotPaths(projection));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default List<T> filterFindAllProjectedBy(Predicate predicate, Sort sort, String... dotPaths) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findAllProjectedBy(ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate),
          sort, dotPaths);
    }
    return findAllProjectedBy(ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate),
        sort, dotPaths);
  }

  default Page<T> filterFindPagedProjectedBy(Pageable pageable, Path<?>... paths) {
    return filterFindPagedProjectedBy(pageable, MongoQuerydslUtils.findDotPaths(paths));
  }

  default Page<T> filterFindPagedProjectedBy(Pageable pageable, Class<?> projection) {
    return filterFindPagedProjectedBy(pageable, MongoQuerydslUtils.findDotPaths(projection));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Page<T> filterFindPagedProjectedBy(Pageable pageable, String... dotPaths) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findPagedProjectedBy(rar.getPredicateOfManageAbility(), pageable, dotPaths);
    }
    return findPagedProjectedBy(rar.getPredicateOfReadAbility(), pageable, dotPaths);
  }

  default Page<T> filterFindPagedProjectedBy(Predicate predicate, Pageable pageable,
      Path<?>... paths) {
    return filterFindPagedProjectedBy(predicate, pageable, MongoQuerydslUtils.findDotPaths(paths));
  }

  default Page<T> filterFindPagedProjectedBy(Predicate predicate, Pageable pageable,
      Class<?> projection) {
    return filterFindPagedProjectedBy(predicate, pageable,
        MongoQuerydslUtils.findDotPaths(projection));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Page<T> filterFindPagedProjectedBy(Predicate predicate, Pageable pageable,
      String... dotPaths) {
    Optional<ResourceAccessRule<?, ?, ?>> rarOpt = findResourceAccessRule();
    ResourceAccessRule rar = rarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (rarOpt.isEmpty() || !user.canRead(rar.getResourceType())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManage(rar.getResourceType())) {
      return findPagedProjectedBy(
          ExpressionUtils.allOf(rar.getPredicateOfManageAbility(), predicate), pageable, dotPaths);
    }
    return findPagedProjectedBy(ExpressionUtils.allOf(rar.getPredicateOfReadAbility(), predicate),
        pageable, dotPaths);
  }

}
