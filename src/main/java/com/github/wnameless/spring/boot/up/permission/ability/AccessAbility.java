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
