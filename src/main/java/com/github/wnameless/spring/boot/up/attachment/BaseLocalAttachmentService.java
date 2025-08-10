package com.github.wnameless.spring.boot.up.attachment;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Collection;
import java.util.HexFormat;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.SneakyThrows;

public abstract class BaseLocalAttachmentService<A extends Attachment<ID>, ID>
    implements AttachmentService<A, ID> {

  abstract public BaseLocalAttachmentRepository<A, ID> attachmentRepository();

  abstract public String getRootFilePath();

  @Override
  public A saveAttachment(A attachment) {
    return attachmentRepository().save(attachment);
  }

  @Override
  public Optional<A> findAttachmentByUri(URI uri) {
    return attachmentRepository().findFirstByUri(uri);
  }

  @Override
  public void deleteAttachment(A attachment) {
    attachmentRepository().delete(attachment);
    new File(attachment.getUri()).delete();
  }

  @Override
  public URI writeData(byte[] data) {
    String filePath = getRootFilePath() + bytesToSHA256(data);
    return saveToFile(filePath, data);
  }

  @SneakyThrows
  private String bytesToSHA256(byte[] input) {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    byte[] hashBytes = digest.digest(input);
    return HexFormat.of().formatHex(hashBytes);
  }

  private URI saveToFile(String filePath, byte[] content) {
    File file = new File(filePath);
    File parentDirectory = file.getParentFile();

    // Create the parent directories if they don't exist
    if (!parentDirectory.exists()) {
      parentDirectory.mkdirs();
    }

    // Write the content to the file
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(content);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return file.toURI();
  }

  @SneakyThrows
  @Override
  public InputStream readData(A attachment) {
    String encodedString = new String(Files.readAllBytes(Paths.get(attachment.getUri())), "UTF-8");
    byte[] decodedBytes = Base64.getDecoder().decode(encodedString.trim());
    InputStream is = new ByteArrayInputStream(decodedBytes);
    return is;
  }

  @Override
  public Optional<Consumer<Collection<A>>> outdatedAttachmentProcedure() {
    return Optional.of(outdated -> {
      outdated.forEach(a -> {
        var file = new File(a.getUri());
        if (file.exists()) {
          file.delete();
        }
        attachmentRepository().delete(a);
      });
    });
  }

}
