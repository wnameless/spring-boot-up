package com.github.wnameless.spring.boot.up.jsf.util;

import static lombok.AccessLevel.PRIVATE;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class JsfSimpleField {
  final List<String> titles;
  final String flattenedKey;
  final String jsonPathKey;
  final boolean array;
  final boolean titled;

  static final String TITLE_PLACEHOLDER = "<none>";

  public String titlesToEnumString() {
    List<String> encoded = new ArrayList<>();
    for (String t : titles) {
      if (t == null || t.isEmpty()) t = TITLE_PLACEHOLDER;
      t = t.replace("_", "__").replace("-", "_");
      encoded.add(t);
    }
    return String.join(" - ", encoded);
  }

  public static List<String> titlesFromEnumString(String enumString) {
    List<String> result = new ArrayList<>();
    if (enumString == null || enumString.isEmpty()) return result;
    String[] parts = enumString.split(" - ", -1);
    for (String s : parts) {
      String decoded = s.replace("__", "\0").replace("_", "-").replace("\0", "_");
      if (TITLE_PLACEHOLDER.equals(decoded)) decoded = "";
      result.add(decoded);
    }
    return result;
  }
}
