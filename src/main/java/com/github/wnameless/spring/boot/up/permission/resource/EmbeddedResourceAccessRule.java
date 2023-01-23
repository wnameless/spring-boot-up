package com.github.wnameless.spring.boot.up.permission.resource;

import java.util.Optional;

public interface EmbeddedResourceAccessRule<ER, ERF extends EmbeddedResourceFilterRepository<ER, T, ID>, FR extends ResourceFilterRepository<T, ID>, T, ID>
    extends ResourceAccessRule<FR, T, ID> {

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
