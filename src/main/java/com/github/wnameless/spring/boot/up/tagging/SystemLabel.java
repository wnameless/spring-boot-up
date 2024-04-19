package com.github.wnameless.spring.boot.up.tagging;

import java.util.function.BooleanSupplier;
import org.springframework.data.annotation.Transient;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SystemLabel implements LabelTemplate<String> {

  String id;

  String groupTitle;

  String labelName;

  String labelColor;

  String entityType;

  String username;

  boolean userEditable;

  @Transient
  @Accessors(fluent = true, chain = false)
  BooleanSupplier userPermissionStock;

}
