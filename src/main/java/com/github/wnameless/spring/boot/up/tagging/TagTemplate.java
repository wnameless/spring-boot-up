package com.github.wnameless.spring.boot.up.tagging;

public interface TagTemplate<UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID> {

  ID getId();

  L getLabelTemplate();

  void setLabelTemplate(L labelTemplate);

  UL getUserLabelTemplate();

  void setUserLabelTemplate(UL userLabelTemplate);

  SystemLabel getSystemLabel();

  void setSystemLabel(SystemLabel systemLabel);

  default void setSystemLabel(SystemLabelTemplate systemLabelTemplate) {
    setSystemLabel(systemLabelTemplate.toSystemLabel());
  }

  ID getEntityId();

  void setEntityId(ID entityId);

  String getUsername();

  void setUsername(String username);

}
