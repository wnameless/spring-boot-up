package com.github.wnameless.spring.boot.up.permission.role;

import java.util.Set;

public interface WebRole {

  Role getRole();

  Set<Role> getMinorRoles();

  boolean isActive();

}
