package com.github.wnameless.spring.boot.up.permission.resource;

import org.springframework.core.ResolvableType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.google.common.base.CaseFormat;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;

public interface QuickResourceAccessRule<EP extends EntityPathBase<T>, RF extends ResourceFilterRepository<T, ID>, T, ID>
    extends ResourceAccessRule<RF, T, ID> {

  Class<RF> getResourceFilterRepositoryType();

  Path<ID> getIdFieldPath();

  Expression<ID> getIdValueExpression(ID id);

  default String getIdFieldName() {
    return "id";
  }

  @SuppressWarnings("unchecked")
  @Override
  default Class<T> getResourceType() {
    ResolvableType resolvableType =
        ResolvableType.forClass(getResourceFilterRepository().getClass()).as(CrudRepository.class);
    return (Class<T>) resolvableType.getGeneric(0).getRawClass();
  }

  @Override
  default String getResourceName() {
    return CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_HYPHEN)
        .convert(getResourceType().getSimpleName());
  }

  @Override
  default RF getResourceFilterRepository() {
    return SpringBootUp.getBean(getResourceFilterRepositoryType());
  }

  EP q();

  default String username() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  @Override
  default Predicate getPredicateOfEntityId(ID id) {
    if (id == null) {
      return Expressions.predicate(Ops.IS_NULL, getIdFieldPath());
    }
    return Expressions.predicate(Ops.EQ, getIdFieldPath(), getIdValueExpression(id));
  }

  @Override
  default Predicate getPredicateOfManageAbility() {
    return Expressions.predicate(Ops.IS_NOT_NULL, getIdFieldPath());
  }

  @Override
  default int getRuleOrder() {
    return 1;
  }

}
