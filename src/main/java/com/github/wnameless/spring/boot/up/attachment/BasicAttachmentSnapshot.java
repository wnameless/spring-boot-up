package com.github.wnameless.spring.boot.up.attachment;

import static lombok.AccessLevel.PRIVATE;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = PRIVATE)
public class BasicAttachmentSnapshot<A extends Attachment<ID>, ID>
    implements AttachmentSnapshot<A, ID> {

  @Builder.Default
  List<A> attachments = new ArrayList<>();

  public BasicAttachmentSnapshot(AttachmentSnapshot<A, ID> attachmentSnapshot) {
    attachments = attachmentSnapshot.getAttachments();
  }

}
