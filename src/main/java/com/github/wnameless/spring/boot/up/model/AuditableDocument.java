package com.github.wnameless.spring.boot.up.model;

import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldNameConstants
public abstract class AuditableDocument {

  @EqualsAndHashCode.Include
  @Id
  protected String id;

  @CreatedDate
  protected LocalDateTime createdAt;

  @LastModifiedDate
  protected LocalDateTime updatedAt;

  @CreatedBy
  protected String createdBy;

  @LastModifiedBy
  protected String updatedBy;

}
