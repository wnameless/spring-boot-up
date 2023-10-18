package com.github.wnameless.spring.boot.up.membership;

import java.util.Set;
import com.github.wnameless.spring.boot.up.permission.role.Role;

public interface Membership<ID> {

  String getMembershipOrganizationName();

  String getUsername();

  Set<Role> getRoles();

  void setRoles(Set<Role> roles);

}
