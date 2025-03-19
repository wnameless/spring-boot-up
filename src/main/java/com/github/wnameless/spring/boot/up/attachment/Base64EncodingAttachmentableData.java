package com.github.wnameless.spring.boot.up.attachment;

import static lombok.AccessLevel.PRIVATE;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class Base64EncodingAttachmentableData implements AttachmentableData {

  final String mimeType;

  final String name;

  final String data;

  public Base64EncodingAttachmentableData(String mimeType, String name, String data) {
    this.mimeType = mimeType;
    this.name = name;
    this.data = data;
  }

  public Base64EncodingAttachmentableData(String rjsfBase64Data) {
    String[] parts = rjsfBase64Data.split(Pattern.quote(";"));

    mimeType = parts[0].split(Pattern.quote(":"))[1];
    String name = parts[1].split(Pattern.quote("="))[1];
    try {
      name =
          URLDecoder.decode(parts[1].split(Pattern.quote("="))[1], StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      name = parts[1].split(Pattern.quote("="))[1];
    }
    this.name = name;
    data = parts[2].split(Pattern.quote(","))[1];
  }

  @Override
  public byte[] getBytes() {
    return data.getBytes();
  }

  @SneakyThrows
  public String toBase64Data() {
    return "data:" + mimeType + ";" + "name="
        + URLEncoder.encode(name, StandardCharsets.UTF_8.name()) + ";base64," + data;
  }

}
