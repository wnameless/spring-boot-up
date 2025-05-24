package com.github.wnameless.spring.boot.up.model;

import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Document
public class BaseAuditableDocument extends AuditableDocument {

  String f1;

  Integer f2;

}
