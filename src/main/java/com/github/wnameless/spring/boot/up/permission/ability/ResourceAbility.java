package com.github.wnameless.spring.boot.up.permission.ability;

import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceFilterRepository;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceFilterRepository;

public interface ResourceAbility extends Ability {

  public static ResourceAbility ofResource(Class<?> resourceType, String abilityName,
      ResourceFilterRepository<?, ?> resourceFilterRepository) {
    return new SimpleResourceAbility(resourceType, abilityName, resourceFilterRepository);
  }

  public static ResourceAbility ofResource(Class<?> resourceType, Ability ability,
      ResourceFilterRepository<?, ?> resourceFilterRepository) {
    return new SimpleResourceAbility(resourceType, ability.getAbilityName(),
        resourceFilterRepository);
  }

  public static ResourceAbility ofEmbeddedResource(Class<?> resourceType, String fieldName,
      String abilityName,
      EmbeddedResourceFilterRepository<?, ?, ?> embeddedResourceFilterRepository) {
    return new SimpleResourceAbility(resourceType, fieldName, abilityName,
        embeddedResourceFilterRepository);
  }

  public static ResourceAbility ofEmbeddedResource(Class<?> resourceType, String fieldName,
      Ability ability, EmbeddedResourceFilterRepository<?, ?, ?> embeddedResourceFilterRepository) {
    return new SimpleResourceAbility(resourceType, fieldName, ability.getAbilityName(),
        embeddedResourceFilterRepository);
  }

  Class<?> getResourceType();

  String getFieldName();

  ResourceFilterRepository<?, ?> getResourceFilterRepository();

  EmbeddedResourceFilterRepository<?, ?, ?> getEmbeddedResourceFilterRepository();

}
