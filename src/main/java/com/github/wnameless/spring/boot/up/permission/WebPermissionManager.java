package com.github.wnameless.spring.boot.up.permission;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import com.github.wnameless.spring.boot.up.permission.ability.ResourceAbility;
import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceAccessRule;
import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceFilterRepository;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceAccessRule;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceFilterRepository;
import com.github.wnameless.spring.boot.up.permission.role.Role;

public interface WebPermissionManager {

  Optional<Class<?>> findResourceTypeByName(String resourceName);

  boolean existsResourceType(Class<?> resourceType);

  Optional<ResourceAccessRule<?, ?, ?>> findUserResourceAccessRuleByRepositoryType(
      @SuppressWarnings("rawtypes") Class<? extends ResourceFilterRepository> repo);

  Optional<EmbeddedResourceAccessRule<?, ?, ?, ?, ?>> findUserEmbeddedResourceAccessRuleByRepositoryType(
      @SuppressWarnings("rawtypes") Class<? extends EmbeddedResourceFilterRepository> repo);

  Set<Role> getUserRoles();

  Set<ResourceAbility> getUserResourceAbilities();

  Set<Role> getAllRoles();

  Map<String, Set<String>> getUserMetadata();

}
