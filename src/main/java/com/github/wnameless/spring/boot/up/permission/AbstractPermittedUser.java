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

}
