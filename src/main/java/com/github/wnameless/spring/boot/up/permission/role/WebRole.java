package com.github.wnameless.spring.boot.up.permission.role;

import java.util.Set;

public interface WebRole extends Rolify {

  Set<Role> getMinorRoles();

  boolean isActive();

}
