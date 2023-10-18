package com.github.wnameless.spring.boot.up.membership;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import com.github.wnameless.spring.boot.up.permission.role.Role;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;

public interface MembershipService<ID> {

  List<? extends MembershipRepository<?, ID>> getMembershipRepositories();

  default List<? extends Membership<ID>> findAllByUsername(String username) {
    var memberships = new ArrayList<Membership<ID>>();

    getMembershipRepositories().forEach(repo -> {
      repo.findAllByUsername(username).forEach(membership -> memberships.add(membership));
    });

    return memberships;
  }

  default List<? extends Membership<ID>> findAllByRoles(Collection<? extends Rolify> rolifies) {
    var memberships = new ArrayList<Membership<ID>>();

    getMembershipRepositories().forEach(repo -> {
      var targetMemberships = repo.findAllByRolesIn(rolifies.stream().map(Rolify::toRole).toList());
      memberships.addAll(targetMemberships);
    });

    return memberships;
  }

  default List<? extends Membership<ID>> findAllByMembershipOrganizationNameAndRoles(
      String membershipOrganizationName, Collection<? extends Rolify> rolifies) {
    var memberships = new ArrayList<Membership<ID>>();

    getMembershipRepositories().forEach(repo -> {
      var targetMemberships = repo.findAllByRolesIn(rolifies.stream().map(Rolify::toRole).toList());
      var filteredMemberships = targetMemberships.stream()
          .filter(mem -> Objects.equals(membershipOrganizationName, mem.getOrganizationBelonging()))
          .toList();
      memberships.addAll(filteredMemberships);
    });

    return memberships;
  }

  default Set<Role> findAllRolesByUsername(String username) {
    var roles = new LinkedHashSet<Role>();

    getMembershipRepositories().forEach(repo -> {
      repo.findAllByUsername(username).forEach(oum -> {
        roles.addAll(oum.getRoles().stream().map(Rolify::toRole).toList());
      });
    });

    return roles;
  }

}
