package com.github.wnameless.spring.boot.up.attachment;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AttachmentGroup {

  public static final AttachmentGroup single(String group) {
    return new AttachmentGroup(group, true);
  }

  public static final AttachmentGroup single(String group, String title) {
    return new AttachmentGroup(group, title, true);
  }

  public static final AttachmentGroup multiple(String group) {
    return new AttachmentGroup(group, false);
  }

  public static final AttachmentGroup multiple(String group, String title) {
    return new AttachmentGroup(group, title, false);
  }

  String group;

  String title;

  boolean single;

  public AttachmentGroup() {}

  public AttachmentGroup(String group, boolean single) {
    this.group = group;
    this.single = single;
  }

  public AttachmentGroup(String group, String title, boolean single) {
    this.group = group;
    this.title = title;
    this.single = single;
  }

  public boolean isMultiple() {
    return !isSingle();
  }

}
