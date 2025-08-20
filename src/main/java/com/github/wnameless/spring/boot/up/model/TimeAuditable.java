package com.github.wnameless.spring.boot.up.model;

import java.time.Instant;

public interface TimeAuditable {

  Instant getCreatedAt();

  void setCreatedAt(Instant createdAt);

  Instant getUpdatedAt();

  void setUpdatedAt(Instant updatedAt);

}
