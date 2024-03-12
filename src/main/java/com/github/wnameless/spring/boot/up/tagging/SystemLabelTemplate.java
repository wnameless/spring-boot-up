package com.github.wnameless.spring.boot.up.tagging;

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

    return systemLabel;
  }

}
