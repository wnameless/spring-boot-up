package com.github.wnameless.spring.boot.up.organizationalunit;

import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationalChart<ID> {

  SimpleOrganizationalUnit<ID> defaultRoot;

  Set<Class<? extends OrganizationalUnit<ID>>> nodeTypes;

  public boolean hasDefaultRoot() {
    return defaultRoot != null;
  }

  public boolean isTreeNode(OrganizationalUnit<ID> organizationalUnit) {
    return Objects.equals(organizationalUnit, defaultRoot)
        || nodeTypes.contains(organizationalUnit.getClass());
  }

}
