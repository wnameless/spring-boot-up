package com.github.wnameless.spring.boot.up.membership;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import com.github.wnameless.spring.boot.up.permission.role.Role;

public interface Membership<ID> {

  ID getMembershipOrganizationId();

  String getUsername();

  Set<Role> getRoles();

  void setRoles(Set<Role> roles);

  default Map<String, ?> getMembershipMetadata() {
    return Collections.emptyMap();
  }

  default boolean isMembershipActive() {
    return true;
  }

}
