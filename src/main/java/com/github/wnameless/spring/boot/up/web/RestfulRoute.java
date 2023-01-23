package com.github.wnameless.spring.boot.up.web;

public interface RestfulRoute<ID> extends JoinablePath {

  public static <ID> RestfulRoute<ID> of(String indexPath) {
    return new RestfulRoute<ID>() {

      @Override
      public String getIndexPath() {
        return indexPath;
      }

    };
  }

  public static <ID> RestfulRoute<ID> of(String indexPath, String templatePath) {
    return new RestfulRoute<ID>() {

      @Override
      public String getIndexPath() {
        return indexPath;
      }

      @Override
      public String getTemplatePath() {
        return templatePath;
      }

    };
  }

  @Override
  default String getRootPath() {
    return getIndexPath();
  }

  default String getTemplatePath() {
    return getIndexPath();
  }

  default String idToParam(ID id) {
    return id.toString();
  }

  String getIndexPath();

  default String getCreatePath() {
    return getIndexPath();
  }

  default String getNewPath() {
    return getIndexPath() + "/new";
  }

  default String getEditPath(ID id) {
    return getIndexPath() + "/" + idToParam(id) + "/edit";
  }

  default String editPath(ID id) {
    return getEditPath(id);
  }

  default String getShowPath(ID id) {
    return getIndexPath() + "/" + idToParam(id);
  }

  default String showPath(ID id) {
    return getShowPath(id);
  }

  default String getUpdatePath(ID id) {
    return getIndexPath() + "/" + idToParam(id);
  }

  default String updatePath(ID id) {
    return getUpdatePath(id);
  }

  default String getDeletePath(ID id) {
    return getIndexPath() + "/" + idToParam(id);
  }

  default String deletePath(ID id) {
    return getDeletePath(id);
  }

  default RestfulRoute<ID> toTemplateRoute() {
    String templatePath = getTemplatePath();
    if (templatePath.startsWith("/")) {
      templatePath = templatePath.replaceFirst("^/+", "");
    }
    String indexPath = templatePath;

    return new RestfulRoute<ID>() {

      @Override
      public String getIndexPath() {
        return indexPath;
      }

    };
  }

}
