package com.github.wnameless.spring.boot.up.tagging;

import java.util.function.BooleanSupplier;

public interface SystemLabelTemplate extends LabelTemplate<String> {

  default SystemLabel toSystemLabel() {
    var systemLabel = new SystemLabel();

    systemLabel.setId(getId());
    systemLabel.setGroupTitle(getGroupTitle());
    systemLabel.setLabelName(getLabelName());
    systemLabel.setLabelColor(getLabelColor());
    systemLabel.setEntityType(getEntityType());
    systemLabel.setUsername(getUsername());
    systemLabel.setUserEditable(isUserEditable());
    systemLabel.userPermissionStock(userPermissionStock());

    return systemLabel;
  }

  default BooleanSupplier userPermissionStock() {
    return null;
  }

  default void userPermissionStock(BooleanSupplier permissionStock) {}

}
