package com.github.wnameless.spring.boot.up.attachment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Random;
import org.springframework.data.repository.CrudRepository;
import lombok.SneakyThrows;

public abstract class BaseLocalAttachmentService<A extends Attachment<ID>, ID>
    implements AttachmentService<A, ID> {

  abstract public CrudRepository<A, ID> attachmentRepository();

  abstract public String getRootFilePath();

  abstract public A newAttachment();

  @Override
  public A saveAttachment(A attachment) {
    return attachmentRepository().save(attachment);
  }

  @Override
  public void deleteAttachment(A attachment) {
    attachmentRepository().delete(attachment);
  }

  @Override
  public URI writeData(byte[] data) {
    String filePath = getRootFilePath() + getRandomCode();
    return saveToFile(filePath, data);
  }

  private String getRandomCode() {
    int letterNumberZero = 48;
    int letterAlphabetLowerZ = 122;
    int codeLength = 32;
    Random random = new Random();

    String generatedCode = random.ints(letterNumberZero, letterAlphabetLowerZ + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(codeLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();

    return generatedCode;
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
    return new FileInputStream(new File(attachment.getUri()));
  }

}
