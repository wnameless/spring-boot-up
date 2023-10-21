package com.github.wnameless.spring.boot.up.organizationalunit;

public interface IdNameSoloOrganizationalUnit<ID> extends SoloOrganizationalUnit<ID> {

  ID getId();

  default ID getOrganizationalUnitId() {
    return getId();
  }

  String getName();

  default String getOrganizationalUnitName() {
    return getName();
  }

}
