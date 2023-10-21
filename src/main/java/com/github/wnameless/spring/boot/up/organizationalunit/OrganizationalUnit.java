package com.github.wnameless.spring.boot.up.organizationalunit;

public interface OrganizationalUnit<ID> {

  ID getOrganizationalUnitId();

  String getOrganizationalUnitName();

  ID getParentOrganizationalUnitId();

}
