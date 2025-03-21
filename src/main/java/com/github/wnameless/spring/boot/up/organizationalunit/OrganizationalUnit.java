package com.github.wnameless.spring.boot.up.organizationalunit;

import java.util.Objects;
import java.util.function.Predicate;

public interface OrganizationalUnit<ID> {

  ID getOrganizationalUnitId();

  String getOrganizationalUnitName();

  ID getParentOrganizationalUnitId();

  default Predicate<? super OrganizationalUnit<ID>> equalityScrutiny() {
    return ou -> Objects.equals(getOrganizationalUnitId(), ou.getOrganizationalUnitId())
        && Objects.equals(getParentOrganizationalUnitId(), ou.getParentOrganizationalUnitId());
  }

}
