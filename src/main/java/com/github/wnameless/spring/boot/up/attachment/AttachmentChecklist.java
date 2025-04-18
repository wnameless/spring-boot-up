package com.github.wnameless.spring.boot.up.attachment;

import static lombok.AccessLevel.PRIVATE;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = PRIVATE)
public class AttachmentChecklist {

  // @Singular
  List<AttachmentGroup> attachmentGroups;

  public Set<String> getGroupNames() {
    return new LinkedHashSet<>(attachmentGroups.stream().map(g -> g.getGroup()).toList());
  }

}
