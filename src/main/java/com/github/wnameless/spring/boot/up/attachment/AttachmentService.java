package com.github.wnameless.spring.boot.up.attachment;

import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import org.springframework.core.GenericTypeResolver;
import lombok.SneakyThrows;

public interface AttachmentService<A extends Attachment<ID>, ID> {

  @SuppressWarnings("unchecked")
  default Class<A> getAttachmentType() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), AttachmentService.class);
    return (Class<A>) genericTypeResolver[0];
  }

  @SneakyThrows
  default A newAttachment() {
    return (A) getAttachmentType().getDeclaredConstructor().newInstance();
  }

  A saveAttachment(A attachment);

  void deleteAttachment(A attachment);

  URI writeData(byte[] data);

  InputStream readData(A attachment);

  Optional<Consumer<Collection<A>>> outdatedAttachmentProcedure();

}
