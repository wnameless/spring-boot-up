package com.github.wnameless.spring.boot.up.membership;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.github.wnameless.spring.boot.up.permission.role.Role;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;

public interface MembershipService<ID> {

  List<? extends MembershipRepository<?, ?, ID>> getMembershipRepositories();

  default Set<Role> findAllRolesByUsername(String username) {
    var roles = new LinkedHashSet<Role>();

    getMembershipRepositories().forEach(repo -> {
      repo.findByUsername(username).ifPresent(oum -> {
        roles.addAll(oum.getRoles().stream().map(Rolify::toRole).toList());
      });
    });

    return roles;
  }

}
