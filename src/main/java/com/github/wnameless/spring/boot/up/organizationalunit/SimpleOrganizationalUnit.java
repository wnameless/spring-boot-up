package com.github.wnameless.spring.boot.up.organizationalunit;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SimpleOrganizationalUnit<ID> implements OrganizationalUnit<ID> {

  ID organizationalUnitId;
  String organizationalUnitName;
  ID parentOrganizationalUnitId = null;

  public SimpleOrganizationalUnit() {}

  public SimpleOrganizationalUnit(OrganizationalUnit<ID> ou) {
    organizationalUnitId = ou.getOrganizationalUnitId();
    organizationalUnitName = ou.getOrganizationalUnitName();
    parentOrganizationalUnitId = ou.getParentOrganizationalUnitId();
  }

  public SimpleOrganizationalUnit(ID organizationalUnitId, String organizationalUnitName) {
    this.organizationalUnitId = organizationalUnitId;
    this.organizationalUnitName = organizationalUnitName;
  }

  public SimpleOrganizationalUnit(ID organizationalUnitId, String organizationalUnitName,
      ID parentOrganizationalUnitId) {
    this.organizationalUnitId = organizationalUnitId;
    this.organizationalUnitName = organizationalUnitName;
    this.parentOrganizationalUnitId = parentOrganizationalUnitId;
  }

}
