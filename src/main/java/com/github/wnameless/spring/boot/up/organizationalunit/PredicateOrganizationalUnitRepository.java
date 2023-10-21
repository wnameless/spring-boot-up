package com.github.wnameless.spring.boot.up.organizationalunit;

import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import com.querydsl.core.types.Predicate;

@NoRepositoryBean
public interface PredicateOrganizationalUnitRepository<OU extends OrganizationalUnit<ID>, ID>
    extends OrganizationalUnitRepository<OU, ID>, QuerydslPredicateExecutor<OU> {

  Function<ID, Predicate> getOrganizationalUnitIdPredicate();

  default Optional<OU> findByOrganizationalUnitId(ID organizationalUnitId) {
    return findOne(getOrganizationalUnitIdPredicate().apply(organizationalUnitId));
  }

}
