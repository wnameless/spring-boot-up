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
        .filter(as -> as.getAttachmentType().equals(getAttachmentType())).findFirst().get();
    getAttachmentSnapshot().getAttachments().forEach(attachment -> {
      attachmentService.deleteAttachment((Attachment) attachment);
    });
  }

}
