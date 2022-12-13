package com.github.wnameless.spring.boot.up.permission.role;

import lombok.Data;
import lombok.NonNull;

@Data
public class SimpleRolifyUser implements RolifyUser {

  @NonNull
  private final String username;

  @NonNull
  private final Role role;

}
