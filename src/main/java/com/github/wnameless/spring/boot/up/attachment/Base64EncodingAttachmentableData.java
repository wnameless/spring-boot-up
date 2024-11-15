package com.github.wnameless.spring.boot.up.attachment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Base64EncodingAttachmentableData implements AttachmentableData {

  String mimeType;

  String name;

  String data;

  public Base64EncodingAttachmentableData(String rjsfBase64Data) {
    String[] parts = rjsfBase64Data.split(Pattern.quote(";"));

    mimeType = parts[0].split(Pattern.quote(":"))[1];
    // name = parts[1].split(Pattern.quote("="))[1];
    try {
      name =
          URLDecoder.decode(parts[1].split(Pattern.quote("="))[1], StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      name = parts[1].split(Pattern.quote("="))[1];
    }
    data = parts[2].split(Pattern.quote(","))[1];
  }

  @Override
  public byte[] getBytes() {
    return data.getBytes();
  }

}
