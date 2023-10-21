package com.github.wnameless.spring.boot.up.organizationalunit;

import java.util.Optional;
import java.util.function.Function;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;

public interface StringIdOrganizationUnitRepository<OU extends OrganizationalUnit<String>>
    extends PredicateOrganizationalUnitRepository<OU, String> {

  @SuppressWarnings("unchecked")
  default Function<String, Predicate> getOrganizationalUnitIdPredicate() {
    Class<OU> entityClass = (Class<OU>) this.getResourceType();
    PathBuilder<OU> entityPath = new PathBuilder<>(entityClass, entityClass.getSimpleName());
    return id -> Expressions.stringPath(entityPath, "id").eq(id);
  }

  default Optional<OU> findByOrganizationalUnitId(String organizationalUnitId) {
    return findOne(getOrganizationalUnitIdPredicate().apply(organizationalUnitId));
  }

}
