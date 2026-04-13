package com.github.wnameless.spring.boot.up.tagging;

import static lombok.AccessLevel.PRIVATE;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import org.springframework.data.annotation.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@EqualsAndHashCode(of = {"id"})
@FieldDefaults(level = PRIVATE)
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

  @Transient
  @Accessors(fluent = true, chain = false)
  Predicate<Object> userPermissionPredicate;

}
