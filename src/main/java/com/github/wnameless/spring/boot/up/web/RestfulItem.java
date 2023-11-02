package com.github.wnameless.spring.boot.up.web;

import org.atteo.evo.inflector.English;
import com.google.common.base.CaseFormat;

public interface RestfulItem<ID> extends JoinablePath {

  default boolean isSingular() {
    return false;
  }

  default String getBackPath() {
    return isSingular() ? getShowPath() : getIndexPath();
  }

  default String getBackPath(QueryConfig<?> queryConfig) {
    return getBackPath(queryConfig, false, false);
  }

  default String getBackPath(QueryConfig<?> queryConfig, boolean excludePage, boolean excludeSort) {
    return getBackPath() + queryConfig.toQueryString(excludePage, excludeSort);
  }

  @Override
  default String getRootPath() {
    return getShowPath();
  }

  ID getId();

  default String getBasePath() {
    String lowerHyphen =
        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, this.getClass().getSimpleName());
    String plural = English.plural(lowerHyphen);
    return "/" + plural;
  }

  default String getIndexPath() {
    return getBasePath();
  }

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
    return getBasePath();
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
    return getBasePath() + "/new";
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

  default String getEditPath() {
    if (isSingular()) {
      return getBasePath() + "/edit";
    } else {
      return getBasePath() + "/" + getId() + "/edit";
    }
  }

  default String getEditPath(QueryConfig<?> queryConfig) {
    return getEditPath(queryConfig, false, false);
  }

  default String getEditPath(QueryConfig<?> queryConfig, boolean excludePage, boolean excludeSort) {
    if (queryConfig != null) {
      return getEditPath() + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getEditPath();
  }

  default String getShowPath() {
    if (isSingular()) {
      return getBasePath();
    } else {
      return getBasePath() + "/" + getId();
    }
  }

  default String getShowPath(QueryConfig<?> queryConfig) {
    return getShowPath(queryConfig, false, false);
  }

  default String getShowPath(QueryConfig<?> queryConfig, boolean excludePage, boolean excludeSort) {
    if (queryConfig != null) {
      return getShowPath() + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getShowPath();
  }

  default String getUpdatePath() {
    if (isSingular()) {
      return getBasePath();
    } else {
      return getBasePath() + "/" + getId();
    }
  }

  default String getUpdatePath(QueryConfig<?> queryConfig) {
    return getUpdatePath(queryConfig, false, false);
  }

  default String getUpdatePath(QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getUpdatePath() + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getUpdatePath();
  }

  default String getDeletePath() {
    if (isSingular()) {
      return getBasePath();
    } else {
      return getBasePath() + "/" + getId();
    }
  }

  default String getDeletePath(QueryConfig<?> queryConfig) {
    return getDeletePath(queryConfig, false, false);
  }

  default String getDeletePath(QueryConfig<?> queryConfig, boolean excludePage,
      boolean excludeSort) {
    if (queryConfig != null) {
      return getDeletePath() + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getDeletePath();
  }

  default RestfulItem<ID> withParent(RestfulItem<?> parent) {
    String basePath = parent.getShowPath() + getBasePath();
    ID id = getId();

    return new RestfulItem<ID>() {

      @Override
      public String getBasePath() {
        return basePath;
      }

      @Override
      public ID getId() {
        return id;
      }

    };
  }

  default <CID> RestfulItem<CID> withChild(RestfulItem<CID> child) {
    String basePath = getShowPath() + child.getBasePath();
    CID id = child.getId();

    return new RestfulItem<CID>() {

      @Override
      public String getBasePath() {
        return basePath;
      }

      @Override
      public CID getId() {
        return id;
      }

    };
  }

}
