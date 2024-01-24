package com.github.wnameless.spring.boot.up.tagging;

public interface TagTemplate<UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID> {

  ID getId();

  L getLabelTemplate();

  UL getUserLabelTemplate();

  ID getEntityId();

  String getUsername();

}
