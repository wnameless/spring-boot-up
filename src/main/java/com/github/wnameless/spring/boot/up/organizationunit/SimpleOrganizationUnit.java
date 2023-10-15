package com.github.wnameless.spring.boot.up.organizationunit;

import lombok.Data;

@Data
public class SimpleOrganizationUnit implements OrganizationUnit {

  private String organizationUnitName;
  private String parentOrganizationUnitName = null;

  public SimpleOrganizationUnit() {}

  public SimpleOrganizationUnit(OrganizationUnit ou) {
    organizationUnitName = ou.getOrganizationUnitName();
    parentOrganizationUnitName = ou.getParentOrganizationUnitName();
  }

  public SimpleOrganizationUnit(String organizationUnitName) {
    this.organizationUnitName = organizationUnitName;
  }

  public SimpleOrganizationUnit(String organizationUnitName, String parentOrganizationUnitName) {
    this.organizationUnitName = organizationUnitName;
    this.parentOrganizationUnitName = parentOrganizationUnitName;
  }

}
