package com.github.wnameless.spring.boot.up.permission.resource;

import org.springframework.core.ResolvableType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.google.common.base.CaseFormat;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;

public interface MongoIdResourceAccessRule<EP extends EntityPathBase<T>, RF extends ResourceFilterRepository<T, String>, T>
    extends ResourceAccessRule<RF, T, String> {

  Class<RF> getResourceFilterRepositoryType();

  @SuppressWarnings("unchecked")
  @Override
  default Class<T> getResourceType() {
    ResolvableType resolvableType =
        ResolvableType.forClass(getResourceFilterRepository().getClass()).as(MongoRepository.class);
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
  default Predicate getPredicateOfEntityId(String id) {
    StringPath idField = Expressions.stringPath(q(), "id");
    if (id == null) {
      return Expressions.predicate(Ops.IS_NULL, idField);
    }
    Expression<String> constant = Expressions.constant(id);
    return Expressions.predicate(Ops.EQ, idField, constant);
  }

  @Override
  default Predicate getPredicateOfManageAbility() {
    StringPath idField = Expressions.stringPath(q(), "id");
    return Expressions.predicate(Ops.IS_NOT_NULL, idField);
  }

  @Override
  default int getRuleOrder() {
    return 1;
  }

}

