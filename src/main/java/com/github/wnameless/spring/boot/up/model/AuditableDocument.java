package com.github.wnameless.spring.boot.up.model;

import java.time.Instant;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AuditableDocument implements TimeAuditable, UserAuditable {

  public static final class Fields {
    public static final String id = "id";
    public static final String createdAt = "createdAt";
    public static final String updatedAt = "updatedAt";
    public static final String createdBy = "createdBy";
    public static final String updatedBy = "updatedBy";
  }

  @EqualsAndHashCode.Include
  @Id
  protected String id;

  @Indexed
  @CreatedDate
  protected Instant createdAt;

  @Indexed
  @LastModifiedDate
  protected Instant updatedAt;

  @Indexed
  @CreatedBy
  protected String createdBy;

  @Indexed
  @LastModifiedBy
  protected String updatedBy;

}
