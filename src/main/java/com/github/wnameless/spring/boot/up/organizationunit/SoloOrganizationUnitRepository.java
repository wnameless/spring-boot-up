package com.github.wnameless.spring.boot.up.organizationunit;

import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface SoloOrganizationUnitRepository<OU extends SoloOrganizationUnit, ID>
    extends OrganizationUnitRepository<OU, ID> {}
