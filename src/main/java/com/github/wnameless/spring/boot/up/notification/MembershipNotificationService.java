package com.github.wnameless.spring.boot.up.notification;

import static java.util.stream.Collectors.toSet;
import java.util.Collection;
import java.util.Set;
import com.github.wnameless.spring.boot.up.membership.MembershipService;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;
import net.sf.rubycollect4j.Ruby;

public interface MembershipNotificationService<MS extends MembershipService<ID>, ID> {

  MS getMembershipService();

  default Set<RolifyNotificationReceiver> findAllByRoles(Collection<? extends Rolify> rolifies) {
    var roles = rolifies.stream().map(Rolify::toRole).toList();
    return getMembershipService().findAllByRoles(roles).stream().map(mem -> {
      var remainRoles = Ruby.Array.copyOf(roles).intersection(mem.getRoles());
      return new RolifyNotificationReceiver(mem.getUsername(), remainRoles);
    }).collect(toSet());
  }

  default Set<RolifyNotificationReceiver> findAllByMembershipOrganizationNameAndRoles(
      String membershipOrganizationName, Collection<? extends Rolify> rolifies) {
    var roles = rolifies.stream().map(Rolify::toRole).toList();
    return getMembershipService()
        .findAllByMembershipOrganizationNameAndRoles(membershipOrganizationName, roles).stream()
        .map(mem -> {
          var remainRoles = Ruby.Array.copyOf(roles).intersection(mem.getRoles());
          return new RolifyNotificationReceiver(mem.getUsername(), remainRoles);
        }).collect(toSet());
  }

}
