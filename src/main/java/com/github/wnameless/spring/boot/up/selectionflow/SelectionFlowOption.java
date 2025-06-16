package com.github.wnameless.spring.boot.up.selectionflow;

import static lombok.AccessLevel.PRIVATE;
import com.github.wnameless.spring.boot.up.organizationalunit.OrganizationalUnit;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class SelectionFlowOption<ID> implements OrganizationalUnit<ID> {

  ID organizationalUnitId;

  String organizationalUnitName;

  boolean selected;

  @Override
  public ID getParentOrganizationalUnitId() {
    return null;
  }

}
