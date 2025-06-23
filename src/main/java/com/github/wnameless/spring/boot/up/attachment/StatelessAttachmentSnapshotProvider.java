package com.github.wnameless.spring.boot.up.attachment;

public interface StatelessAttachmentSnapshotProvider<A extends Attachment<ID>, ID>
    extends AttachmentSnapshotProvider<A, ID> {

  @Override
  default void saveAttachmentSnapshotProvider() {}

  @Override
  default void afterDeleteFromMongo() {}

}
