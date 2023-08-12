package com.github.wnameless.spring.boot.up.attachment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public interface AttachmentSnapshot<A extends Attachment<ID>, ID> {

  ID getId();

  void setId(ID id);

  List<A> getAttachments();

  void setAttachments(List<A> attachments);

  LocalDateTime getCreatedAt();

  void setCreatedAt(LocalDateTime createdAt);

  LocalDateTime getUpdatedAt();

  void setUpdatedAt(LocalDateTime updatedAt);

  default Optional<A> findAttachment(ID id) {
    if (getAttachments() == null) return Optional.empty();
    return getAttachments().stream().filter(a -> Objects.equals(a.getId(), id)).findFirst();
  }

  default Map<String, List<A>> getAttachmentsByGroup() {
    var map = new LinkedHashMap<String, List<A>>();
    if (getAttachments() != null) {
      getAttachments().forEach(a -> {
        if (!map.containsKey(a.getGroup())) {
          map.put(a.getGroup(), new ArrayList<>());
        }
        map.get(a.getGroup()).add(a);
      });
    }
    return map;
  }

}
