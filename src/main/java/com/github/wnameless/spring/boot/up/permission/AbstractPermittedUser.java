package com.github.wnameless.spring.boot.up.permission;

import java.util.HashSet;
import java.util.Set;
import com.github.wnameless.spring.boot.up.permission.ability.ResourceAbility;
import com.github.wnameless.spring.boot.up.permission.role.Role;
import jakarta.annotation.PostConstruct;
import lombok.Data;

@Data
public abstract class AbstractPermittedUser<ID> implements PermittedUser<ID> {

  private final Set<Role> allRoles = new HashSet<>();
  private final Set<ResourceAbility> allResourceAbilities = new HashSet<>();

  abstract protected Set<Role> getUserRoles();

  abstract protected Set<ResourceAbility> getUserResourceAbilities();

  @PostConstruct
  protected void init() {
    allRoles.addAll(getUserRoles());
    allResourceAbilities.addAll(getUserResourceAbilities());
  }

  public boolean containsRole(Role role) {
    return allRoles.contains(role.toRole());
  }

  public boolean containsAllRoles(Role... roles) {
    boolean hasAll = true;
    for (Role role : roles) {
      if (!allRoles.contains(role.toRole())) return false;
    }
    return hasAll;
  }

  public boolean containsAnyRole(Role... roles) {
    boolean hasAny = false;
    for (Role role : roles) {
      if (allRoles.contains(role.toRole())) return true;
    }
    return hasAny;
  }

}
