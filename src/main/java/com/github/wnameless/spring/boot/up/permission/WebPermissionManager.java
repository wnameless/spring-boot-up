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
package com.github.wnameless.spring.boot.up.permission;

import java.util.Map;
import java.util.Set;
import com.github.wnameless.spring.boot.up.permission.ability.ResourceAbility;
import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceAccessRule;
import com.github.wnameless.spring.boot.up.permission.resource.EmbeddedResourceFilterRepository;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceAccessRule;
import com.github.wnameless.spring.boot.up.permission.resource.ResourceFilterRepository;
import com.github.wnameless.spring.boot.up.permission.role.Role;

public interface WebPermissionManager {

  Class<?> findResourceTypeByName(String resourceName);

  ResourceAccessRule<?, ?, ?> findUserResourceAccessRuleByRepositoryType(
      @SuppressWarnings("rawtypes") Class<? extends ResourceFilterRepository> repo);

  EmbeddedResourceAccessRule<?, ?, ?, ?, ?> findUserEmbeddedResourceAccessRuleByRepositoryType(
      @SuppressWarnings("rawtypes") Class<? extends EmbeddedResourceFilterRepository> repo);

  Set<Role> getUserRoles();

  Set<ResourceAbility> getUserResourceAbilities();

  Set<Role> getAllRoles();

  Map<String, Set<String>> getUserMetadata();

}
