package com.github.wnameless.spring.boot.up.attachment;

import java.util.Objects;
import com.github.wnameless.spring.boot.up.core.DataModelCRUDTrigger;

public interface AttachmentSnapshotAware<T, A extends Attachment<ID>, ID>
    extends DataModelCRUDTrigger<T> {

  AttachmentSnapshot<A, ID> getSnapshot();

  AttachmentChecklist getChecklist();

  default void saveAttachmentSnapshotAware() {
    updateThisDataModel();
  }

  default boolean isValidAttachment(A attachment) {
    return getChecklist().getAttachmentGroups().stream().filter(g -> {
      return Objects.equals(attachment.getGroup(), g.getGroup());
    }).findAny().isPresent();
  }

  default boolean isExistedAttachment(A attachment) {
    if (getSnapshot().getAttachments() == null) return false;
    return getSnapshot().getAttachments().stream().filter(a -> {
      return Objects.equals(a.getGroup(), attachment.getGroup())
          && Objects.equals(a.getName(), attachment.getName());
    }).findAny().isPresent();
  }

  default boolean removeExistedAttachment(A attachment) {
    if (getSnapshot().getAttachments() == null) return false;
    return getSnapshot().getAttachments().removeIf(a -> {
      return Objects.equals(a.getGroup(), attachment.getGroup())
          && Objects.equals(a.getName(), attachment.getName());
    });
  }

}
