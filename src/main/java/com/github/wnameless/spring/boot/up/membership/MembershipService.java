package com.github.wnameless.spring.boot.up.membership;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import com.github.wnameless.spring.boot.up.permission.role.Role;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;

public interface MembershipService<ID> {

  List<? extends MembershipRepository<?, ID>> getMembershipRepositories();

  default List<? extends Membership<ID>> findAllByMembershipTypes(
      Collection<Class<? extends Membership<ID>>> membershipTypes) {
    var memberships = new ArrayList<Membership<ID>>();

    getMembershipRepositories().forEach(repo -> {
      if (membershipTypes.contains(repo.getMembershipType())) {
        repo.findAll().forEach(membership -> memberships.add(membership));
      }
    });

    return memberships;
  }

  default List<? extends Membership<ID>> findAllByUsername(String username) {
    var memberships = new ArrayList<Membership<ID>>();

    getMembershipRepositories().forEach(repo -> {
      repo.findAllByUsername(username).forEach(membership -> memberships.add(membership));
    });

    return memberships;
  }

  default List<? extends Membership<ID>> findAllByUsernameAndMembershipType(String username,
      Class<? extends Membership<ID>> membershipType) {
    var memberships = new ArrayList<Membership<ID>>();

    getMembershipRepositories().forEach(repo -> {
      if (Objects.equals(repo.getMembershipType(), membershipType)) {
        repo.findAllByUsername(username).forEach(membership -> memberships.add(membership));
      }
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

  default List<? extends Membership<ID>> findAllByMembershipOrganizationIdAndRoles(
      ID membershipOrganizationId, Collection<? extends Rolify> rolifies) {
    var memberships = new ArrayList<Membership<ID>>();

    getMembershipRepositories().forEach(repo -> {
      var targetMemberships = repo.findAllByRolesIn(rolifies.stream().map(Rolify::toRole).toList());
      var filteredMemberships = targetMemberships.stream()
          .filter(
              mem -> Objects.equals(membershipOrganizationId, mem.getMembershipOrganizationId()))
          .toList();
      memberships.addAll(filteredMemberships);
    });

    return memberships;
  }

  default List<? extends Membership<ID>> findAllByMembershipOrganizationIdAndUsernameAndRoles(
      ID membershipOrganizationId, String username, Collection<? extends Rolify> rolifies) {
    var memberships = new ArrayList<Membership<ID>>();

    getMembershipRepositories().forEach(repo -> {
      var targetMemberships = repo.findAllByRolesIn(rolifies.stream().map(Rolify::toRole).toList());
      var filteredMemberships = targetMemberships.stream()
          .filter(mem -> Objects.equals(membershipOrganizationId, mem.getMembershipOrganizationId())
              && Objects.equals(username, mem.getUsername()))
          .toList();
      memberships.addAll(filteredMemberships);
    });

    return memberships;
  }

  default Set<Role> findAllRolesByUsername(String username) {
    var roles = new LinkedHashSet<Role>();

    getMembershipRepositories().forEach(repo -> {
      repo.findAllByUsername(username).forEach(membership -> {
        roles.addAll(Optional.ofNullable(membership.getRoles()).orElse(Set.of()));
      });
    });

    return roles;
  }

  default List<? extends Membership<ID>> findAllByUsernameAndRolesIn(String username,
      Collection<? extends Rolify> rolifies) {
    var memberships = new ArrayList<Membership<ID>>();

    getMembershipRepositories().forEach(repo -> {
      var targetMemberships = repo.findAllByUsernameAndRolesIn(username,
          rolifies.stream().map(Rolify::toRole).toList());
      memberships.addAll(targetMemberships);
    });

    return memberships;
  }

}
