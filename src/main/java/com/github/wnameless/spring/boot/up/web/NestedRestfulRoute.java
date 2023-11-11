package com.github.wnameless.spring.boot.up.web;

public class NestedRestfulRoute<ID> implements RestfulRoute<ID> {

  private String parentPath;
  private String childPath;

  public NestedRestfulRoute(String parentPath, String childPath) {
    this.parentPath = parentPath;
    this.childPath = childPath;
  }

  @Override
  public String getIndexPath() {
    return JoinablePath.of(parentPath).joinPath(childPath);
  }

  @Override
  public String getTemplatePath() {
    return childPath;
  }

}
