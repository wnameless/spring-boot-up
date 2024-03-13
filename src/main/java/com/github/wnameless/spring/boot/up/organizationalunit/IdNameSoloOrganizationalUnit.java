package com.github.wnameless.spring.boot.up.organizationalunit;

import com.github.wnameless.spring.boot.up.web.IdProvider;

public interface IdNameSoloOrganizationalUnit<ID>
    extends SoloOrganizationalUnit<ID>, IdProvider<ID> {

  default ID getOrganizationalUnitId() {
    return getId();
  }

  String getName();

  default String getOrganizationalUnitName() {
    return getName();
  }

}
