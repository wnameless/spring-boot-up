package com.github.wnameless.spring.boot.up.permission.ability;

import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceAccessRule;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceAccessRule;

public interface AccessAbility extends Ability {

  public static AccessAbility ofResource(Class<?> resourceType, String abilityName,
      Class<? extends ResourceAccessRule<?, ?, ?>> resourceAccessRuleType) {
    return new ResourceAccessAbility(resourceType, null, abilityName, resourceAccessRuleType);
  }

  public static AccessAbility ofEmbeddedResource(Class<?> resourceType, String fieldName,
      String abilityName,
      Class<? extends EmbeddedResourceAccessRule<?, ?, ?, ?, ?>> embeddedResourceAccessRuleType) {
    return new ResourceAccessAbility(resourceType, fieldName, abilityName,
        embeddedResourceAccessRuleType);
  }

  Class<?> getResourceType();

  String getFieldName();

  Class<? extends ResourceAccessRule<?, ?, ?>> getResourceAccessRuleType();

  default boolean isResourceEmbedded() {
    return getFieldName() != null;

  }

}
