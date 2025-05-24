package com.github.wnameless.spring.boot.up.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldNameConstants
public abstract class AuditableDocument implements TimeAuditable, UserAuditable {

  @EqualsAndHashCode.Include
  @Id
  protected String id;

  @Indexed
  @CreatedDate
  protected LocalDateTime createdAt;

  @Indexed
  @LastModifiedDate
  protected LocalDateTime updatedAt;

  @Indexed
  @CreatedBy
  protected String createdBy;

  @Indexed
  @LastModifiedBy
  protected String updatedBy;

}
