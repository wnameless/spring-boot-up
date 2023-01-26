package com.github.wnameless.spring.boot.up.permission;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.security.core.context.SecurityContextHolder;
import com.github.wnameless.spring.boot.up.permission.role.Role;

public interface PermittedUser<ID>
    extends UserResourceAbility<ID>, UserEmbeddedResourceAbility<ID> {

  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(PermittedUser.class);

  default String getUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  Set<Role> getAllRoles();

  default boolean hasRole(String role) {
    return getAllRoles().contains(Role.of(role));
  }

  default boolean hasAllRoles(String... roles) {
    boolean hasAll = true;
    Set<Role> allRoles = getAllRoles();
    for (String role : roles) {
      if (!allRoles.contains(Role.of(role))) return false;
    }
    return hasAll;
  }

  default boolean hasAnyRole(String... roles) {
    boolean hasAny = false;
    Set<Role> allRoles = getAllRoles();
    for (String role : roles) {
      if (allRoles.contains(Role.of(role))) return true;
    }
    return hasAny;
  }

  Map<String, Set<String>> getUserMetadata();

  default Set<String> getUserMeta(String key) {
    Set<String> meta = getUserMetadata().get(key);
    return meta == null ? new LinkedHashSet<>() : meta;
  }

}
