package com.github.wnameless.spring.boot.up.organizationunit;

import com.github.wnameless.spring.boot.up.permission.role.Rolify;

public interface OrganizationUnitMemberAware<OU extends OrganizationUnit, R extends Rolify, ID> {

  Class<? extends OrganizationUnitMemberRepository<OU, R, ID>> getOrganizationUnitMemberRepositoryType();

}
