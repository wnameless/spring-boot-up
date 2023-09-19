package com.github.wnameless.spring.boot.up.permission.role;

public interface Rolify {

  String getRoleName();

  default Role toRole() {
    return new Role(getRoleName());
  }

}
