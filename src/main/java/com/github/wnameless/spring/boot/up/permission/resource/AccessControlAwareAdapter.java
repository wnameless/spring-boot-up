package com.github.wnameless.spring.boot.up.permission.resource;

public interface AccessControlAwareAdapter extends AccessControlAware {

  ForwardableAccessControlAware getForwardableAccessControlAware();

  default AccessControlRule getManageable() {
    return getForwardableAccessControlAware().getForwardingManageable();
  }

  default AccessControlRule getCrudable() {
    return getForwardableAccessControlAware().getForwardingCrudable();
  }

  default AccessControlRule getCreatable() {
    return getForwardableAccessControlAware().getForwardingCreatable();
  }

  default AccessControlRule getReadable() {
    return getForwardableAccessControlAware().getForwardingReadable();
  }

  default AccessControlRule getUpdatable() {
    return getForwardableAccessControlAware().getForwardingUpdatable();
  }

  default AccessControlRule getDeletable() {
    return getForwardableAccessControlAware().getForwardingDeletable();
  }

}
