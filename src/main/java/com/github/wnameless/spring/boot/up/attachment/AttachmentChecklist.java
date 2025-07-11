package com.github.wnameless.spring.boot.up.attachment;

import static lombok.AccessLevel.PRIVATE;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@FieldDefaults(level = PRIVATE)
public class AttachmentChecklist {

  // @Singular
  @Builder.Default
  List<AttachmentGroup> attachmentGroups = new ArrayList<>();

  public Set<String> getGroupNames() {
    return new LinkedHashSet<>(attachmentGroups.stream().map(g -> g.getGroup()).toList());
  }

}
