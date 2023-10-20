package com.github.wnameless.spring.boot.up.membership;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.querydsl.ListQuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import com.github.wnameless.spring.boot.up.permission.role.Role;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;

@NoRepositoryBean
public interface PredicateMembershipRepository<M extends Membership<ID>, ID>
    extends MembershipRepository<M, ID>, ListQuerydslPredicateExecutor<M> {

  Function<String, Predicate> getUsernamePredicate();

  Function<Collection<Role>, Predicate> getRolesPredicate();

  default List<M> findAllByUsername(String username) {
    return findAll(getUsernamePredicate().apply(username));
  }

  default List<M> findAllByRolesIn(Collection<Role> roles) {
    return findAll(getRolesPredicate().apply(roles));
  }

  default List<M> findAllByUsernameAndRolesIn(String username, Collection<Role> roles) {
    return findAll(ExpressionUtils.allOf(getUsernamePredicate().apply(username),
        getRolesPredicate().apply(roles)));
  }

}
