package com.github.wnameless.spring.boot.up.organizationunit;

import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import com.querydsl.core.types.Predicate;

@NoRepositoryBean
public interface PredicateOrganizationUnitRepository<OU extends OrganizationUnit, ID>
    extends OrganizationUnitRepository<OU, ID>, QuerydslPredicateExecutor<OU> {

  Function<String, Predicate> getOrganizationUnitNamePredicate();

  default Optional<OU> findByOrganizationUnitName(String organizationUnitName) {
    return findOne(getOrganizationUnitNamePredicate().apply(organizationUnitName));
  }

}
