package com.github.wnameless.spring.boot.up.permission.resource;

public interface ForwardableAccessControlAware {

  default AccessControlRule getForwardingManageable() {
    return new AccessControlRule(false, () -> true);
  }

  default AccessControlRule getForwardingCrudable() {
    return new AccessControlRule(false, () -> true);
  }

  default AccessControlRule getForwardingCreatable() {
    return new AccessControlRule(false, () -> true);
  }

  default AccessControlRule getForwardingReadable() {
    return new AccessControlRule(false, () -> true);
  }

  default AccessControlRule getForwardingUpdatable() {
    return new AccessControlRule(false, () -> true);
  }

  default AccessControlRule getForwardingDeletable() {
    return new AccessControlRule(false, () -> true);
  }

}
