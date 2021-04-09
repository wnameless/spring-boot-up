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
package com.github.wnameless.spring.boot.up.permission.resource;

import java.util.Optional;

public interface EmbeddedResourceAccessRule<ER, ERF extends EmbeddedResourceFilterRepository<ER, T, ID>, T, ID, FR extends ResourceFilterRepository<T, ID>>
    extends ResourceAccessRule<T, ID, FR> {

  ERF getEmbeddedResourceFilterRepository();

  String getEmbeddedResourceFieldName();

  ER getEmbeddedResource(T entity);

  default Optional<ER> getEmbeddedResource(Optional<T> entity) {
    if (entity.isPresent()) {
      return Optional.ofNullable(getEmbeddedResource(entity.get()));
    } else {
      return Optional.empty();
    }
  }

  void setEmbeddedResource(T entity, ER embeddedResource);

}
