package com.github.wnameless.spring.boot.up.notification;

import static java.util.stream.Collectors.toSet;
import java.util.Collection;
import java.util.Set;
import com.github.wnameless.spring.boot.up.permission.role.Role;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;
import lombok.Data;

@Data
public class RolifyNotificationReceiver implements NotificationReceiver<Set<Role>> {

  private String username;

  private Set<Role> userMeta;

  public RolifyNotificationReceiver() {}

  public RolifyNotificationReceiver(String username, Collection<? extends Rolify> userMeta) {
    this.username = username;
    this.userMeta = userMeta.stream().map(Rolify::toRole).collect(toSet());
  }

}
