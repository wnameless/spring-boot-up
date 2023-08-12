package com.github.wnameless.spring.boot.up.attachment;

import java.io.InputStream;
import java.net.URI;

public interface AttachmentService<A extends Attachment<ID>, ID> {

  A newAttachment();

  A saveAttachment(A attachment);

  void deleteAttachment(A attachment);

  URI writeData(byte[] data);

  InputStream readData(A attachment);

}
