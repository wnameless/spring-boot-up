package com.github.wnameless.spring.boot.up.attachment;

import static lombok.AccessLevel.PRIVATE;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class AttachmentGroup {

  public static final AttachmentGroup single(String group) {
    return new AttachmentGroup(group, true);
  }

  public static final AttachmentGroup multiple(String group) {
    return new AttachmentGroup(group, false);
  }

  String group;

  boolean single;

  boolean required = false;

  public AttachmentGroup() {}

  public AttachmentGroup(String group, boolean single) {
    this.group = group;
    this.single = single;
  }

  public boolean isMultiple() {
    return !isSingle();
  }

  public AttachmentGroup withRequired(boolean required) {
    this.required = required;
    return this;
  }

}
