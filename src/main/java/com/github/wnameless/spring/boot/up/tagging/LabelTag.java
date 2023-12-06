package com.github.wnameless.spring.boot.up.tagging;

public interface LabelTag<TT extends TagTemplate, ID> {

  TT getTagTemplate();

  ID getEntityId();

  String getUsername();

}
