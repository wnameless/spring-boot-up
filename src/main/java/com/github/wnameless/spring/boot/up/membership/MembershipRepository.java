package com.github.wnameless.spring.boot.up.membership;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import com.github.wnameless.spring.boot.up.permission.role.Role;

@NoRepositoryBean
public interface MembershipRepository<M extends Membership<ID>, ID> extends CrudRepository<M, ID> {

  default Optional<M> findByMemberOrganizationIdAndUsername(ID membershipOrganizationId,
      String username) {
    return findAllByUsername(username).stream()
        .filter(m -> Objects.equals(membershipOrganizationId, m.getMembershipOrganizationId()))
        .findFirst();
  }

  List<M> findAllByUsername(String username);

  List<M> findAllByRolesIn(Collection<Role> roles);

  List<M> findAllByUsernameAndRolesIn(String username, Collection<Role> roles);

}
