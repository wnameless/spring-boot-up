package com.github.wnameless.spring.boot.up.web;

import org.atteo.evo.inflector.English;
import com.google.common.base.CaseFormat;

public interface RestfulItem<ID> extends JoinablePath {

  default boolean isSingular() {
    return false;
  }

  default boolean hasBackPathname() {
    return getBackPathname() != null && !getBackPathname().trim().isEmpty();
  }

  default String getBackPathname() {
    return null;
  }

  default String getBackPath() {
    return hasBackPathname() ? getBackPathname() : getIndexPath();
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

  default String getCreatePath() {
    return getBasePath();
  }

  default String getNewPath() {
    return getBasePath() + "/new";
  }

  default String getEditPath() {
    if (isSingular()) {
      return getBasePath() + "/edit";
    } else {
      return getBasePath() + "/" + getId() + "/edit";
    }
  }

  default String getShowPath() {
    if (isSingular()) {
      return getBasePath();
    } else {
      return getBasePath() + "/" + getId();
    }
  }

  default String getUpdatePath() {
    if (isSingular()) {
      return getBasePath();
    } else {
      return getBasePath() + "/" + getId();
    }
  }

  default String getDeletePath() {
    if (isSingular()) {
      return getBasePath();
    } else {
      return getBasePath() + "/" + getId();
    }
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
