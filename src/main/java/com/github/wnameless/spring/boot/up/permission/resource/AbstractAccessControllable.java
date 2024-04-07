package com.github.wnameless.spring.boot.up.permission.resource;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
public class AbstractAccessControllable implements AccessControllable {

  AccessControlRule manageable = new AccessControlRule(false, () -> true);
  AccessControlRule crudable = new AccessControlRule(false, () -> true);
  AccessControlRule readable = new AccessControlRule(false, () -> true);
  AccessControlRule updatable = new AccessControlRule(false, () -> true);
  AccessControlRule deletable = new AccessControlRule(false, () -> true);

}
