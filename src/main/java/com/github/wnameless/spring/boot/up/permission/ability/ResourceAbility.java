/*
 *
 * Copyright 2020 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
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
