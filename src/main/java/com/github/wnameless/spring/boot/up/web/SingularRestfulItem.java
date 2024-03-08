package com.github.wnameless.spring.boot.up.web;

public interface SingularRestfulItem<ID> extends RestfulItem<ID> {

  @Override
  default boolean isSingular() {
    return true;
  }

}
