package com.github.wnameless.spring.boot.up.attachment;

import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterDeleteFromMongo;
import com.github.wnameless.spring.boot.up.model.DataModelCRUDTrigger;
import jakarta.persistence.PostRemove;

public interface QuickAttachmentSnapshotProvider<T, A extends Attachment<ID>, ID>
    extends AttachmentSnapshotProvider<A, ID>, DataModelCRUDTrigger<T> {

  @Override
  default void saveAttachmentSnapshotProvider() {
    updateThisDataModel();
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @PostRemove // JPA
  @AfterDeleteFromMongo // MongoDB
  default void afterDeleteFromMongo() {
    var attachmentService = SpringBootUp.getBeansOfType(AttachmentService.class).values().stream()
        // Check exact match OR if either type is assignable from the other
        // IMPORTANT: isAssignableFrom() handles CGLIB proxies correctly
        .filter(as -> as.getAttachmentType().equals(getAttachmentType()) ||
                      as.getAttachmentType().isAssignableFrom(getAttachmentType()) ||
                      getAttachmentType().isAssignableFrom(as.getAttachmentType()))
        .findFirst().get();
    getAttachmentSnapshot().getAttachments().forEach(attachment -> {
      attachmentService.deleteAttachment((Attachment) attachment);
    });
  }

}
