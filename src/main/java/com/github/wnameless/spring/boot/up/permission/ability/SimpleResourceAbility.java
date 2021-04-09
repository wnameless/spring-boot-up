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

import lombok.Data;
import lombok.NonNull;

@Data
public class SimpleResourceAbility implements ResourceAbility {

  private final Class<?> resourceType;
  private final String fieldName;
  private final String abilityName;

  private final ResourceFilterRepository<?, ?> resourceFilterRepository;
  private final EmbeddedResourceFilterRepository<?, ?, ?> embeddedResourceFilterRepository;

  public SimpleResourceAbility(@NonNull Class<?> resourceType,
      @NonNull String abilityName,
      @NonNull ResourceFilterRepository<?, ?> resourceFilterRepository) {
    this.resourceType = resourceType;
    this.fieldName = null;
    this.abilityName = abilityName.toUpperCase();
    this.resourceFilterRepository = resourceFilterRepository;
    this.embeddedResourceFilterRepository = null;
  }

  public SimpleResourceAbility(@NonNull Class<?> resourceType,
      @NonNull String fieldName, @NonNull String abilityName,
      @NonNull EmbeddedResourceFilterRepository<?, ?, ?> embeddedResourceFilterRepository) {
    this.resourceType = resourceType;
    this.fieldName = fieldName;
    this.abilityName = abilityName.toUpperCase();
    this.resourceFilterRepository = null;
    this.embeddedResourceFilterRepository = embeddedResourceFilterRepository;
  }

}
