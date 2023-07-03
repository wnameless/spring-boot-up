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

  default String getIndexPath(QueryConfig<?> queryConfig) {
    return getIndexPath(queryConfig, false, false);
  }

  default String getIndexPath(QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getIndexPath() + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getIndexPath();
  }

  default String getCreatePath() {
    return getIndexPath();
  }

  default String getCreatePath(QueryConfig<?> queryConfig) {
    return getCreatePath(queryConfig, false, false);
  }

  default String getCreatePath(QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getCreatePath() + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getCreatePath();
  }

  default String getNewPath() {
    return getIndexPath() + "/new";
  }

  default String getNewPath(QueryConfig<?> queryConfig) {
    return getNewPath(queryConfig, false, false);
  }

  default String getNewPath(QueryConfig<?> queryConfig, boolean excludePage, boolean excludeSort) {
    if (queryConfig != null) {
      return getNewPath() + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getNewPath();
  }

  default String getEditPath(ID id) {
    return getIndexPath() + "/" + idToParam(id) + "/edit";
  }

  default String getEditPath(ID id, QueryConfig<?> queryConfig) {
    return getEditPath(id, queryConfig, false, false);
  }

  default String getEditPath(ID id, QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getEditPath(id) + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getEditPath(id);
  }

  default String editPath(ID id) {
    return getEditPath(id);
  }

  default String editPath(ID id, QueryConfig<?> queryConfig) {
    return getEditPath(id, queryConfig, false, false);
  }

  default String editPath(ID id, QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getEditPath(id) + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getEditPath(id);
  }

  default String getShowPath(ID id) {
    return getIndexPath() + "/" + idToParam(id);
  }

  default String getShowPath(ID id, QueryConfig<?> queryConfig) {
    return getShowPath(id, queryConfig, false, false);
  }

  default String getShowPath(ID id, QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getShowPath(id) + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getShowPath(id);
  }

  default String showPath(ID id) {
    return getShowPath(id);
  }

  default String showPath(ID id, QueryConfig<?> queryConfig) {
    return getShowPath(id, queryConfig, false, false);
  }

  default String showPath(ID id, QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getShowPath(id) + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getShowPath(id);
  }

  default String getUpdatePath(ID id) {
    return getIndexPath() + "/" + idToParam(id);
  }

  default String getUpdatePath(ID id, QueryConfig<?> queryConfig) {
    return getUpdatePath(id, queryConfig, false, false);
  }

  default String getUpdatePath(ID id, QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getUpdatePath(id) + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getUpdatePath(id);
  }

  default String updatePath(ID id) {
    return getUpdatePath(id);
  }

  default String updatePath(ID id, QueryConfig<?> queryConfig) {
    return getUpdatePath(id, queryConfig, false, false);
  }

  default String updatePath(ID id, QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getUpdatePath(id) + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getUpdatePath(id);
  }

  default String getDeletePath(ID id) {
    return getIndexPath() + "/" + idToParam(id);
  }

  default String getDeletePath(ID id, QueryConfig<?> queryConfig) {
    return getDeletePath(id, queryConfig, false, false);
  }

  default String getDeletePath(ID id, QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getDeletePath(id) + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getDeletePath(id);
  }

  default String deletePath(ID id) {
    return getDeletePath(id);
  }

  default String deletePath(ID id, QueryConfig<?> queryConfig) {
    return getDeletePath(id, queryConfig, false, false);
  }

  default String deletePath(ID id, QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getDeletePath(id) + queryConfig.toQueryString(excludePage, excludeSort);
    }
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
