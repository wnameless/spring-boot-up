package com.github.wnameless.spring.boot.up.web;

import java.util.function.Supplier;

public interface SingularRestfulRoute<ID> extends RestfulRoute<ID> {

  public static <I> SingularRestfulRoute<I> of(String indexPath) {
    return new SingularRestfulRoute<>() {

      @Override
      public String getIndexPath() {
        return indexPath;
      }

    };
  }

  public static <I> SingularRestfulRoute<I> of(String indexPath, String templatePath) {
    return new SingularRestfulRoute<>() {

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

  public static <I> SingularRestfulRoute<I> of(Supplier<String> indexPathStock,
      Supplier<String> templatePathStock) {
    return new SingularRestfulRoute<>() {

      @Override
      public String getIndexPath() {
        return indexPathStock.get();
      }

      @Override
      public String getTemplatePath() {
        return templatePathStock.get();
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
  default String idToParam(ID id) {
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
  default String getEditPath(ID id) {
    return getIndexPath() + "/edit";
  }

  @Override
  default String editPath(ID id) {
    return getEditPath(id);
  }

  @Override
  default String getShowPath(ID id) {
    return getIndexPath() + "/" + idToParam(id);
  }

  @Override
  default String showPath(ID id) {
    return getShowPath(id);
  }

  @Override
  default String getUpdatePath(ID id) {
    return getIndexPath();
  }

  @Override
  default String updatePath(ID id) {
    return getUpdatePath(id);
  }

  @Override
  default String getDeletePath(ID id) {
    return getIndexPath();
  }

  @Override
  default String deletePath(ID id) {
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
