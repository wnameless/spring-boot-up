package com.github.wnameless.spring.boot.up.permission.resource;

public interface ForwardingAccessControllable extends AccessControllable {

  AccessControllable accessControllable();

  default AccessControlRule getManageable() {
    return accessControllable().getManageable();
  }

  default AccessControlRule getCrudable() {
    return accessControllable().getCrudable();
  }

  default AccessControlRule getCreatable() {
    return accessControllable().getCreatable();
  }

  default AccessControlRule getReadable() {
    return accessControllable().getReadable();
  }

  default AccessControlRule getUpdatable() {
    return accessControllable().getUpdatable();
  }

  default AccessControlRule getDeletable() {
    return accessControllable().getDeletable();
  }

}
