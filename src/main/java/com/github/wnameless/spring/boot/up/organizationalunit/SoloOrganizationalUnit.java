package com.github.wnameless.spring.boot.up.organizationalunit;

public interface SoloOrganizationalUnit<ID> extends OrganizationalUnit<ID> {

  default ID getParentOrganizationalUnitId() {
    return null;
  }

}
