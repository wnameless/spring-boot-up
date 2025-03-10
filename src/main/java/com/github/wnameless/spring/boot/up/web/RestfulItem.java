package com.github.wnameless.spring.boot.up.web;

import java.util.Objects;
import java.util.Optional;
import org.atteo.evo.inflector.English;
import com.github.wnameless.apt.INamedResource;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.google.common.base.CaseFormat;

public interface RestfulItem<ID> extends JoinablePath, IdProvider<ID> {

  default String getResourceName() {
    if (SpringBootUp.applicationContext() != null) {
      Optional<INamedResource> nr =
          SpringBootUp.findAllGenericBeans(INamedResource.class).stream().filter(n -> {
            var itemClassName = this.getClass().getName();
            String nrClassName = null;
            try {
              nrClassName = n.getClass().getDeclaredField("CLASS_NAME").get(n).toString();
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
                | SecurityException e) {}
            return Objects.equals(itemClassName, nrClassName);
          }).findAny();

      if (nr.isPresent()) {
        try {
          String resourceName =
              nr.get().getClass().getDeclaredField("RESOURCE").get(nr.get()).toString();
          if (resourceName != null) {
            return resourceName;
          }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
            | SecurityException e) {}
      }
    }

    String lowerHyphen =
        CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, this.getClass().getSimpleName());
    return lowerHyphen;
  }

  default String getResourcesName() {
    if (SpringBootUp.applicationContext() != null) {
      Optional<INamedResource> nr =
          SpringBootUp.findAllGenericBeans(INamedResource.class).stream().filter(n -> {
            var itemClassName = this.getClass().getName();
            String nrClassName = null;
            try {
              nrClassName = n.getClass().getDeclaredField("CLASS_NAME").get(n).toString();
            } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
                | SecurityException e) {}
            return Objects.equals(itemClassName, nrClassName);
          }).findAny();

      if (nr.isPresent()) {
        try {
          String resourcesName =
              nr.get().getClass().getDeclaredField("RESOURCES").get(nr.get()).toString();
          if (resourcesName != null) {
            return resourcesName;
          }
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException
            | SecurityException e) {}
      }
    }

    return English.plural(getResourceName());
  }

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
    if (queryConfig != null) {
      return getBackPath() + queryConfig.toQueryString(excludePage, excludeSort);
    }
    return getBackPath();
  }

  @Override
  default String getRootPath() {
    return getShowPath();
  }

  default String getBasePath() {
    return "/" + (isSingular() ? getResourceName() : getResourcesName());
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
