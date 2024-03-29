package com.github.wnameless.spring.boot.up.organizationalmembership;

import static java.util.stream.Collectors.toList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import com.github.wnameless.spring.boot.up.membership.Membership;
import com.github.wnameless.spring.boot.up.membership.MembershipService;
import com.github.wnameless.spring.boot.up.organizationalunit.OrganizationalUnit;
import com.github.wnameless.spring.boot.up.organizationalunit.OrganizationalUnitService;
import com.github.wnameless.spring.boot.up.permission.role.Rolify;

public interface OrganizationalMembershipService<ID> {

  OrganizationalUnitService<ID> getOrganizationalUnitService();

  MembershipService<ID> getMembershipService();

  default List<ID> findAllOrganizationalUnitIds(String username, Rolify... rolifies) {
    return findAllOrganizationalUnitIds(username, rolifies);
  }

  default List<ID> findAllOrganizationalUnitIds(String username,
      Collection<? extends Rolify> rolifies) {
    return getMembershipService().findAllByUsernameAndRolesIn(username, rolifies).stream()
        .map(Membership::getMembershipOrganizationId).collect(toList());
  }

  default List<ID> findAllChildOrganizationalUnitIds(String username, Rolify... rolifies) {
    return findAllChildOrganizationalUnitIds(username, List.of(rolifies));
  }

  default List<ID> findAllChildOrganizationalUnitIds(String username,
      Collection<? extends Rolify> rolifies) {
    var parentIds = getMembershipService().findAllByUsernameAndRolesIn(username, rolifies).stream()
        .map(Membership::getMembershipOrganizationId).collect(toList());
    return getOrganizationalUnitService().findAllOrganizationalUnits().stream()
        .filter(ou -> parentIds.contains(ou.getParentOrganizationalUnitId()))
        .map(OrganizationalUnit::getOrganizationalUnitId).collect(toList());
  }

  default List<ID> findAllChildOrganizationalUnitIds(String username, Rolify[] rolifies,
      Collection<Class<? extends OrganizationalUnit<ID>>> resourceTypes) {
    return findAllChildOrganizationalUnitIds(username, List.of(rolifies), resourceTypes);
  }

  default List<ID> findAllChildOrganizationalUnitIds(String username,
      Collection<? extends Rolify> rolifies,
      Collection<Class<? extends OrganizationalUnit<ID>>> resourceTypes) {
    var parentIds = getMembershipService().findAllByUsernameAndRolesIn(username, rolifies).stream()
        .map(Membership::getMembershipOrganizationId).toList();
    return getOrganizationalUnitService().findAllOrganizationalUnitsByResourceTypes(resourceTypes)
        .stream().filter(ou -> parentIds.contains(ou.getParentOrganizationalUnitId()))
        .map(OrganizationalUnit::getOrganizationalUnitId).collect(toList());
  }

  default List<ID> findAllParentOrganizationalUnitIds(String username, Rolify... rolifies) {
    return findAllParentOrganizationalUnitIds(username, List.of(rolifies));
  }

  default List<ID> findAllParentOrganizationalUnitIds(String username,
      Collection<? extends Rolify> rolifies) {
    var childIds = getMembershipService().findAllByUsernameAndRolesIn(username, rolifies).stream()
        .map(Membership::getMembershipOrganizationId).collect(toList());
    return getOrganizationalUnitService().findAllOrganizationalUnits(childIds).stream()
        .flatMap(Optional::stream).map(OrganizationalUnit::getParentOrganizationalUnitId)
        .collect(toList());
  }

}
