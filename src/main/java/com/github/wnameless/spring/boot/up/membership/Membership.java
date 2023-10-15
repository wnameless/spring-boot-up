package com.github.wnameless.spring.boot.up.membership;

import java.util.Set;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;

public interface Membership<R extends Rolify, ID> {

  String getUsername();

  Set<R> getRoles();

}
