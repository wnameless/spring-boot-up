package com.github.wnameless.spring.boot.up.attachment;

import java.net.URI;
import java.time.LocalDateTime;
import com.github.wnameless.spring.boot.up.web.IdProvider;
import com.google.common.base.Strings;

public interface Attachment<ID> extends IdProvider<ID> {

  void setId(ID id);

  String getGroup();

  void setGroup(String group);

  String getName();

  void setName(String name);

  URI getUri();

  void setUri(URI uri);

  String getNote();

  void setNote(String note);

  LocalDateTime getCreatedAt();

  void setCreatedAt(LocalDateTime createdAt);

  default String getNameWithNote() {
    String note = Strings.nullToEmpty(getNote());
    return "" + getName() + (note.isBlank() ? "" : " - " + note);
  }

}
