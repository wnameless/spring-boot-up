package com.github.wnameless.spring.boot.up.organizationunit;

import java.util.Set;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;

public interface OrganizationUnitMember<OU extends OrganizationUnit, R extends Rolify, ID> {

  String getUsername();

  OU getOrganizationUnit();

  Set<R> getRoles();

}
