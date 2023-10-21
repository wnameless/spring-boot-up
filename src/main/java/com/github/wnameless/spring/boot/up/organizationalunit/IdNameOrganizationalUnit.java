package com.github.wnameless.spring.boot.up.organizationalunit;

public interface IdNameOrganizationalUnit<ID> extends OrganizationalUnit<ID> {

  ID getId();

  default ID getOrganizationalUnitId() {
    return getId();
  }

  String getName();

  default String getOrganizationalUnitName() {
    return getName();
  }

}
