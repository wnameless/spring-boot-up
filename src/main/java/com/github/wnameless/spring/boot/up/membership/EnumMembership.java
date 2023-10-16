package com.github.wnameless.spring.boot.up.membership;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import com.github.wnameless.spring.boot.up.permission.role.Role;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;

public interface EnumMembership<M extends Enum<? extends Rolify>, ID> extends Membership<ID> {

  Class<M> getEnumRoleType();

  default Set<M> getEnumRoles() {
    Set<Role> roles = getRoles();
    if (roles == null) return Collections.emptySet();

    Set<M> enumRoles = new LinkedHashSet<>();
    roles.forEach(r -> {
      Arrays.asList(getEnumRoleType().getEnumConstants()).stream()
          .filter(e -> e.name().equals(r.getRoleName())).findFirst()
          .ifPresent(e -> enumRoles.add(e));;
    });
    return enumRoles;
  }

  default void setEnumRoles(Set<M> enumRoles) {
    setRoles(enumRoles.stream().map(r -> Role.of(r.name())).collect(Collectors.toSet()));
  }

}
