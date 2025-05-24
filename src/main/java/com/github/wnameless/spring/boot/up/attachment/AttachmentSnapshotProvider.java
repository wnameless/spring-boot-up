package com.github.wnameless.spring.boot.up.attachment;

import java.util.Objects;
import java.util.Optional;
import org.springframework.core.GenericTypeResolver;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterDeleteFromMongo;
import com.github.wnameless.spring.boot.up.model.DataModelCRUDTrigger;
import jakarta.persistence.PostRemove;

public interface AttachmentSnapshotProvider<T, A extends Attachment<ID>, ID>
    extends DataModelCRUDTrigger<T> {

  @SuppressWarnings({"unchecked", "null"})
  default Class<A> getAttachmentType() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), AttachmentSnapshotProvider.class);
    return (Class<A>) genericTypeResolver[1];
  }

  AttachmentSnapshot<A, ID> getAttachmentSnapshot();

  AttachmentChecklist getAttachmentChecklist();

  default void saveAttachmentSnapshotProvider() {
    updateThisDataModel();
  }

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

  @SuppressWarnings({"rawtypes", "unchecked"})
  @PostRemove // JPA
  @AfterDeleteFromMongo // MongoDB
  default void afterDeleteFromMongo() {
    var attachmentService = SpringBootUp.getBeansOfType(AttachmentService.class).values().stream()
        .filter(as -> as.getAttachmentType().equals(getAttachmentType())).findFirst().get();
    getAttachmentSnapshot().getAttachments().forEach(attachment -> {
      attachmentService.deleteAttachment((Attachment) attachment);
    });
  }

}
