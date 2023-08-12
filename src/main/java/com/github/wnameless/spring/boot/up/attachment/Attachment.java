package com.github.wnameless.spring.boot.up.attachment;

import java.net.URI;
import java.time.LocalDateTime;

public interface Attachment<ID> {

  ID getId();

  void setId(ID id);

  String getGroup();

  void setGroup(String group);

  String getName();

  void setName(String name);

  URI getUri();

  void setUri(URI uri);

  LocalDateTime getCreatedAt();

  void setCreatedAt(LocalDateTime createdAt);

}
