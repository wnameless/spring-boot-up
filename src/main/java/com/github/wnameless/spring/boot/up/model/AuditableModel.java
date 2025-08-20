package com.github.wnameless.spring.boot.up.model;

import java.time.Instant;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AuditableModel implements TimeAuditable, UserAuditable {

  @CreatedDate
  protected Instant createdAt;

  @LastModifiedDate
  protected Instant updatedAt;

  @CreatedBy
  protected String createdBy;

  @LastModifiedBy
  protected String updatedBy;

}
