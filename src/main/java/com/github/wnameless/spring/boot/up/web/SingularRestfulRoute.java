package com.github.wnameless.spring.boot.up.web;

public interface SingularRestfulRoute extends RestfulRoute<Void> {

  public static SingularRestfulRoute of(String indexPath) {
    return new SingularRestfulRoute() {

      @Override
      public String getIndexPath() {
        return indexPath;
      }

    };
  }

  @Override
  default String getRootPath() {
    return getIndexPath();
  }

  @Override
  default String getTemplatePath() {
    return getIndexPath();
  }

  @Override
  default String idToParam(Void id) {
    return "";
  }

  @Override
  String getIndexPath();

  @Override
  default String getCreatePath() {
    return getIndexPath();
  }

  @Override
  default String getNewPath() {
    return getIndexPath() + "/new";
  }

  @Override
  default String getEditPath(Void id) {
    return getIndexPath() + "/edit";
  }

  @Override
  default String editPath(Void id) {
    return getEditPath(id);
  }

  @Override
  default String getShowPath(Void id) {
    return getIndexPath() + "/" + idToParam(id);
  }

  @Override
  default String showPath(Void id) {
    return getShowPath(id);
  }

  @Override
  default String getUpdatePath(Void id) {
    return getIndexPath();
  }

  @Override
  default String updatePath(Void id) {
    return getUpdatePath(id);
  }

  @Override
  default String getDeletePath(Void id) {
    return getIndexPath();
  }

  @Override
  default String deletePath(Void id) {
    return getDeletePath(id);
  }

  // Methods for Singlur Restful Route Only
  default String getEditPath() {
    return getIndexPath() + "/edit";
  }

  default String getShowPath() {
    return getIndexPath();
  }

  default String getUpdatePath() {
    return getIndexPath();
  }

  default String getDeletePath() {
    return getIndexPath();
  }

}
