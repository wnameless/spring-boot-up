package com.github.wnameless.spring.boot.up.permission.resource;

public interface ForwardingAccessControlAware extends AccessControlAware {

  AccessControlAware delegate();

  default AccessControlRule getManageable() {
    return delegate().getManageable();
  }

  default AccessControlRule getCrudable() {
    return delegate().getCrudable();
  }

  default AccessControlRule getCreatable() {
    return delegate().getCreatable();
  }

  default AccessControlRule getReadable() {
    return delegate().getReadable();
  }

  default AccessControlRule getUpdatable() {
    return delegate().getUpdatable();
  }

  default AccessControlRule getDeletable() {
    return delegate().getDeletable();
  }

}
