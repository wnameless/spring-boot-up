package com.github.wnameless.spring.boot.up.organizationalunit;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import org.springframework.data.querydsl.ListQuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

@NoRepositoryBean
public interface PredicateOrganizationalUnitRepository<OU extends OrganizationalUnit<ID>, ID>
    extends OrganizationalUnitRepository<OU, ID>, ListQuerydslPredicateExecutor<OU> {

  Function<ID, Predicate> getOrganizationalUnitIdPredicate();

  default Optional<OU> findByOrganizationalUnitId(ID organizationalUnitId) {
    return findOne(getOrganizationalUnitIdPredicate().apply(organizationalUnitId));
  }

  default List<OU> findAllByOrganizationalUnitIds(Collection<ID> organizationalUnitIds) {
    return findAll(Expressions.allOf(
        organizationalUnitIds.stream().map(id -> getOrganizationalUnitIdPredicate().apply(id))
            .toArray(BooleanExpression[]::new)));
  }

}
