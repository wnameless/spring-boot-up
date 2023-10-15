package com.github.wnameless.spring.boot.up.organizationunit;

public interface SoloOrganizationUnit extends OrganizationUnit {

  default String getParentOrganizationUnitName() {
    return null;
  }

}
