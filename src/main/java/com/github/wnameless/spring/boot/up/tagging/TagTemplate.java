package com.github.wnameless.spring.boot.up.tagging;

import com.github.wnameless.spring.boot.up.web.IdProvider;

public interface TagTemplate<UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID>
    extends IdProvider<ID> {

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

  default String getGroupTitle() {
    if (getLabelTemplate() != null) return getLabelTemplate().getGroupTitle();
    if (getUserLabelTemplate() != null) return getUserLabelTemplate().getGroupTitle();
    if (getSystemLabel() != null) return getSystemLabel().getGroupTitle();
    return null;
  }

  default String getLabelName() {
    if (getLabelTemplate() != null) return getLabelTemplate().getLabelName();
    if (getUserLabelTemplate() != null) return getUserLabelTemplate().getLabelName();
    if (getSystemLabel() != null) return getSystemLabel().getLabelName();
    return null;
  }

  default String getLabelColor() {
    if (getLabelTemplate() != null) return getLabelTemplate().getLabelColor();
    if (getUserLabelTemplate() != null) return getUserLabelTemplate().getLabelColor();
    if (getSystemLabel() != null) return getSystemLabel().getLabelColor();
    return null;
  }

}
