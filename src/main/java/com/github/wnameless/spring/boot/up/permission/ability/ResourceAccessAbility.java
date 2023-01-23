package com.github.wnameless.spring.boot.up.permission.ability;

import com.github.wnameless.spring.boot.up.permission.resource.ResourceAccessRule;
import lombok.Data;
import lombok.NonNull;

@Data
public class ResourceAccessAbility implements AccessAbility {

  private final Class<?> resourceType;
  private final String fieldName;
  private final String abilityName;
  private final Class<? extends ResourceAccessRule<?, ?, ?>> resourceAccessRuleType;

  public ResourceAccessAbility(@NonNull Class<?> resourceType, String fieldName,
      @NonNull String abilityName,
      @NonNull Class<? extends ResourceAccessRule<?, ?, ?>> resourceAccessRuleType) {
    this.resourceType = resourceType;
    this.fieldName = fieldName;
    this.abilityName = abilityName.toUpperCase();
    this.resourceAccessRuleType = resourceAccessRuleType;
  }

}
