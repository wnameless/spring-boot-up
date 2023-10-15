package com.github.wnameless.spring.boot.up.organizationunit;

import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Singular;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrganizationTree {

  SimpleOrganizationUnit defaultRoot;

  @Singular
  Set<Class<? extends OrganizationUnit>> nodeTypes;

  public boolean hasDefaultRoot() {
    return defaultRoot != null;
  }

  public boolean isTreeNode(OrganizationUnit organizationUnit) {
    return Objects.equals(organizationUnit, defaultRoot)
        || nodeTypes.contains(organizationUnit.getClass());
  }

}
