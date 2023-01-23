package com.github.wnameless.spring.boot.up.permission.role;

public interface GlobalRole extends WebRole {

  @Override
  default boolean isActive() {
    return true;
  }

}
