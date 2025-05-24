package com.github.wnameless.spring.boot.up.model;

import java.time.LocalDateTime;

public interface TimeAuditable {

  LocalDateTime getCreatedAt();

  void setCreatedAt(LocalDateTime createdAt);

  LocalDateTime getUpdatedAt();

  void setUpdatedAt(LocalDateTime updatedAt);

}
