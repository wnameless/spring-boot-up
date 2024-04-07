package com.github.wnameless.spring.boot.up.permission.resource;

public interface AccessControllable {

  default AccessControlRule getManageable() {
    return new AccessControlRule(false, () -> true);
  }

  default AccessControlRule getCrudable() {
    return new AccessControlRule(false, () -> true);
  }

  default AccessControlRule getCreatable() {
    return new AccessControlRule(false, () -> true);
  }

  default AccessControlRule getReadable() {
    return new AccessControlRule(false, () -> true);
  }

  default AccessControlRule getUpdatable() {
    return new AccessControlRule(false, () -> true);
  }

  default AccessControlRule getDeletable() {
    return new AccessControlRule(false, () -> true);
  }

}
