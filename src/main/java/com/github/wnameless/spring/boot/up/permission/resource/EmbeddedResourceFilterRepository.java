package com.github.wnameless.spring.boot.up.permission.resource;

import java.util.Optional;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.permission.PermittedUser;
import com.github.wnameless.spring.boot.up.permission.WebPermissionManager;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import net.sf.rubycollect4j.Ruby;

@NoRepositoryBean
public interface EmbeddedResourceFilterRepository<ER, T, ID>
    extends CrudRepository<T, ID>, QuerydslPredicateExecutor<T> {

  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(EmbeddedResourceFilterRepository.class);

  @SuppressWarnings("rawtypes")
  default Optional<EmbeddedResourceAccessRule> findEmbeddedResourceAccessRule() {
    WebPermissionManager wpm = SpringBootUp.getBean(WebPermissionManager.class);
    Optional<EmbeddedResourceAccessRule<?, ?, ?, ?, ?>> erarOpt =
        wpm.findUserEmbeddedResourceAccessRuleByRepositoryType(this.getClass());
    if (erarOpt.isEmpty()) {
      log.warn("User {} with roles: {} don't have enough permission on {}",
          getCurrentUser().getUsername(), getCurrentUser().getAllRoles(),
          AopProxyUtils.proxiedUserInterfaces(this)[0].getSimpleName());
    }
    return erarOpt.isEmpty() ? Optional.empty() : Optional.of(erarOpt.get());
  }

  default PermittedUser<ID> getCurrentUser() {
    @SuppressWarnings("unchecked")
    PermittedUser<ID> user = SpringBootUp.getBean(PermittedUser.class);
    return user;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Optional<ER> embeddedFilterFindOne(Predicate predicate) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return erar.getEmbeddedResource(
          findOne(ExpressionUtils.allOf(erar.getPredicateOfManageAbility(), predicate)));
    }
    return erar.getEmbeddedResource(
        findOne(ExpressionUtils.allOf(erar.getPredicateOfReadAbility(), predicate)));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<ER> embeddedFilterFindAll() {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return Ruby.LazyEnumerator.of(findAll(erar.getPredicateOfManageAbility()))
          .map(e -> (ER) erar.getEmbeddedResource(e));
    }
    return Ruby.LazyEnumerator.of(findAll(erar.getPredicateOfReadAbility()))
        .map(e -> (ER) erar.getEmbeddedResource(e));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<ER> embeddedFilterFindAll(Predicate predicate) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return Ruby.LazyEnumerator
          .of(findAll(ExpressionUtils.allOf(erar.getPredicateOfManageAbility(), predicate)))
          .map(e -> (ER) erar.getEmbeddedResource(e));
    }
    return Ruby.LazyEnumerator
        .of(findAll(ExpressionUtils.allOf(erar.getPredicateOfReadAbility(), predicate)))
        .map(e -> (ER) erar.getEmbeddedResource(e));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<ER> embeddedFilterFindAll(Sort sort) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return Ruby.LazyEnumerator.of(findAll(erar.getPredicateOfManageAbility(), sort))
          .map(e -> (ER) erar.getEmbeddedResource(e));
    }
    return Ruby.LazyEnumerator.of(findAll(erar.getPredicateOfReadAbility(), sort))
        .map(e -> (ER) erar.getEmbeddedResource(e));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<ER> embeddedFilterFindAll(Predicate predicate, Sort sort) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return Ruby.LazyEnumerator
          .of(findAll(ExpressionUtils.allOf(erar.getPredicateOfManageAbility(), predicate), sort))
          .map(e -> (ER) erar.getEmbeddedResource(e));
    }
    return Ruby.LazyEnumerator
        .of(findAll(ExpressionUtils.allOf(erar.getPredicateOfReadAbility(), predicate), sort))
        .map(e -> (ER) erar.getEmbeddedResource(e));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<ER> embeddedFilterFindAll(OrderSpecifier<?>... orders) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return Ruby.LazyEnumerator
          .of(findAll(ExpressionUtils.allOf(erar.getPredicateOfManageAbility()), orders))
          .map(e -> (ER) erar.getEmbeddedResource(e));
    }
    return Ruby.LazyEnumerator
        .of(findAll(ExpressionUtils.allOf(erar.getPredicateOfReadAbility()), orders))
        .map(e -> (ER) erar.getEmbeddedResource(e));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Iterable<ER> embeddedFilterFindAll(Predicate predicate, OrderSpecifier<?>... orders) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return Ruby.LazyEnumerator
          .of(findAll(ExpressionUtils.allOf(erar.getPredicateOfManageAbility(), predicate), orders))
          .map(e -> (ER) erar.getEmbeddedResource(e));
    }
    return Ruby.LazyEnumerator
        .of(findAll(ExpressionUtils.allOf(erar.getPredicateOfReadAbility(), predicate), orders))
        .map(e -> (ER) erar.getEmbeddedResource(e));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Page<ER> embeddedFilterFindAll(Pageable pageable) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return findAll(erar.getPredicateOfManageAbility(), pageable)
          .map(e -> (ER) erar.getEmbeddedResource(e));
    }
    return findAll(erar.getPredicateOfReadAbility(), pageable)
        .map(e -> (ER) erar.getEmbeddedResource(e));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default Page<ER> embeddedFilterFindAll(Predicate predicate, Pageable pageable) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return findAll(ExpressionUtils.allOf(erar.getPredicateOfManageAbility(), predicate), pageable)
          .map(e -> (ER) erar.getEmbeddedResource(e));
    }
    return findAll(ExpressionUtils.allOf(erar.getPredicateOfReadAbility(), predicate), pageable)
        .map(e -> (ER) erar.getEmbeddedResource(e));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default long embeddedFilterCount() {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return count(erar.getPredicateOfManageAbility());
    }
    return count(erar.getPredicateOfReadAbility());
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default long embeddedFilterCount(Predicate predicate) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return count(ExpressionUtils.allOf(erar.getPredicateOfManageAbility(), predicate));
    }
    return count(ExpressionUtils.allOf(erar.getPredicateOfReadAbility(), predicate));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default boolean embeddedFilterExists(Predicate predicate) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canReadField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to READ");
    }

    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      return exists(ExpressionUtils.allOf(erar.getPredicateOfManageAbility(), predicate));
    }
    return exists(ExpressionUtils.allOf(erar.getPredicateOfReadAbility(), predicate));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default ER embeddedFilterSaveById(ID id, ER embedded) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    Predicate idEq = erar.getPredicateOfEntityId(id);
    Optional<T> target = findOne(idEq);
    if (erarOpt.isEmpty() || target.isEmpty()) {
      throw new IllegalArgumentException("Entity ID of the embedded resource NOT found");
    }

    T entity = target.get();
    ER dbEmbedded = (ER) erar.getEmbeddedResource(entity);

    if (dbEmbedded == null) {
      if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
        target = findOne(ExpressionUtils.allOf(erar.getPredicateOfManageAbility(), idEq));
      } else {
        target = findOne(ExpressionUtils.allOf(erar.getPredicateOfCreateAbility(), idEq));
      }

      if (target.isEmpty()) {
        throw new UnsupportedOperationException("No permission to CREATE");
      }

      erar.setEmbeddedResource(entity, embedded);
      entity = save(entity);
      return (ER) erar.getEmbeddedResource(entity);
    } else {
      if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
        target = findOne(ExpressionUtils.allOf(erar.getPredicateOfManageAbility(), idEq));
      } else {
        target = findOne(ExpressionUtils.allOf(erar.getPredicateOfUpdateAbility(), idEq));
      }

      if (target.isEmpty()) {
        throw new UnsupportedOperationException("No permission to UPDATE");
      }

      erar.setEmbeddedResource(entity, embedded);
      entity = save(entity);
      return (ER) erar.getEmbeddedResource(entity);
    }
  }

  default Optional<ER> embeddedFilterFindById(ID id) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    if (erarOpt.isEmpty()) return Optional.empty();
    @SuppressWarnings("unchecked")
    Predicate idEq = erarOpt.get().getPredicateOfEntityId(id);
    return embeddedFilterFindOne(idEq);
  }

  default boolean embeddedFilterExistsById(ID id) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    if (erarOpt.isEmpty()) return false;
    @SuppressWarnings("unchecked")
    Predicate idEq = erarOpt.get().getPredicateOfEntityId(id);
    return embeddedFilterExists(idEq);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default void embeddedFilterDeleteById(ID id) {
    Optional<EmbeddedResourceAccessRule> erarOpt = findEmbeddedResourceAccessRule();
    EmbeddedResourceAccessRule erar = erarOpt.orElse(null);
    PermittedUser user = getCurrentUser();
    if (erarOpt.isEmpty()
        || !user.canDeleteField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      throw new UnsupportedOperationException("No permission to DELETE");
    }

    Predicate idEq = erar.getPredicateOfEntityId(id);
    Optional<T> target;
    if (user.canManageField(erar.getResourceType(), erar.getEmbeddedResourceFieldName())) {
      target = findOne(ExpressionUtils.allOf(erar.getPredicateOfManageAbility(), idEq));
    } else {
      target = findOne(ExpressionUtils.allOf(erar.getPredicateOfDeleteAbility(), idEq));
    }

    if (target.isPresent()) {
      T entity = target.get();
      erar.setEmbeddedResource((T) entity, null);
      save(entity);
    }
  }

}
