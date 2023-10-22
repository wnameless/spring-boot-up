package com.github.wnameless.spring.boot.up.organizationalunit;

import java.util.function.Function;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

public interface StringIdOrganizationalUnitRepository<OU extends OrganizationalUnit<String>>
    extends PredicateOrganizationalUnitRepository<OU, String> {

  @SuppressWarnings("unchecked")
  default Function<String, Predicate> getOrganizationalUnitIdPredicate() {
    Class<OU> entityClass = (Class<OU>) this.getResourceType();
    PathBuilder<OU> entityPath = new PathBuilder<>(entityClass, entityClass.getSimpleName());
    return id -> Expressions.stringPath(entityPath, "id").eq(id);
  }

  @SuppressWarnings("unchecked")
  default Function<String, Predicate> getOrganizationalUnitNamePredicate() {
    Class<OU> entityClass = (Class<OU>) this.getResourceType();
    PathBuilder<OU> entityPath = new PathBuilder<>(entityClass, entityClass.getSimpleName());
    return name -> Expressions.stringPath(entityPath, "name").eq(name);
  }

}
