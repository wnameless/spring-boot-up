package com.github.wnameless.spring.boot.up.attachment;

import java.util.Objects;
import java.util.Optional;
import org.springframework.core.GenericTypeResolver;

public interface AttachmentSnapshotProvider<A extends Attachment<ID>, ID> {

  @SuppressWarnings("unchecked")
  default Class<A> getAttachmentType() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), AttachmentSnapshotProvider.class);
    return (Class<A>) genericTypeResolver[0];
  }

  AttachmentSnapshot<A, ID> getAttachmentSnapshot();

  AttachmentChecklist getAttachmentChecklist();

  void saveAttachmentSnapshotProvider();

  default boolean isValidAttachment(A attachment) {
    return getAttachmentChecklist().getAttachmentGroups().stream().filter(g -> {
      return Objects.equals(attachment.getGroup(), g.getGroup());
    }).findAny().isPresent();
  }

  default boolean isExistedAttachment(A attachment) {
    if (getAttachmentSnapshot().getAttachments() == null) return false;
    return getAttachmentSnapshot().getAttachments().stream().filter(a -> {
      return Objects.equals(a.getGroup(), attachment.getGroup())
          && Objects.equals(a.getName(), attachment.getName());
    }).findAny().isPresent();
  }

  default A removeExistedAttachment(A attachment) {
    if (getAttachmentSnapshot().getAttachments() == null) return null;

    Optional<A> removedAttachment = getAttachmentSnapshot().getAttachments().stream().filter(a -> {
      boolean isRemoved = Objects.equals(a.getGroup(), attachment.getGroup())
          && Objects.equals(a.getName(), attachment.getName());
      return isRemoved;
    }).findFirst();
    removedAttachment.ifPresent(a -> {
      getAttachmentSnapshot().getAttachments().remove(removedAttachment.get());
    });

    return removedAttachment.orElse(null);
  }

  void afterDeleteFromMongo();

}
