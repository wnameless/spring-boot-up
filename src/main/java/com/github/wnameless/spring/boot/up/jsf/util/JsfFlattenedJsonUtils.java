package com.github.wnameless.spring.boot.up.jsf.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsfFlattenedJsonUtils {

  public LinkedHashMap<Object, String> schemaKeyToEnumToNames(String key, String schemaJson) {
    var docCtx = schemaJsonToDocumentContext(schemaJson);

    var enumToNames = new LinkedHashMap<Object, String>();
    var keyParts = keyToKeyParts(key);

    String propertyName = null;
    while (!keyParts.get(keyParts.size() - 1).equals("properties")
        && !keyParts.get(keyParts.size() - 1).equals("items")) {
      propertyName = keyParts.remove(keyParts.size() - 1);
    }
    if (keyParts.get(keyParts.size() - 1).equals("properties")) {
      keyParts.add(propertyName);
    }

    var enumList = docCtx.read("$." + keyParts.stream().collect(Collectors.joining(".")) + ".enum",
        List.class);
    var enumNames = docCtx
        .read("$." + keyParts.stream().collect(Collectors.joining(".")) + ".enumNames", List.class);
    if (enumList != null) {
      for (int i = 0; i < enumList.size(); i++) {
        if (enumNames != null && enumNames.size() > i) {
          enumToNames.put(enumList.get(i), enumNames.get(i).toString());
        } else {
          enumToNames.put(enumList.get(i), null);
        }
      }
    }

    return enumToNames;
  }

  public boolean isSchemaKeyRequired(String key, String schemaJson) {
    var docCtx = schemaJsonToDocumentContext(schemaJson);

    var keyParts = keyToKeyParts(key);
    String propertyName = null;
    while (!keyParts.get(keyParts.size() - 1).equals("properties")) {
      propertyName = keyParts.remove(keyParts.size() - 1);
    }

    keyParts.remove(keyParts.size() - 1);
    keyParts.add("required");

    return docCtx.read("$." + keyParts.stream().collect(Collectors.joining(".")) + "[?(@ == '"
        + propertyName + "')]", List.class).size() > 0;
  }

  public DocumentContext schemaJsonToDocumentContext(String schemaJson) {
    Configuration config = Configuration.defaultConfiguration()
        .addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS);
    DocumentContext docCtx = JsonPath.using(config).parse(schemaJson);
    return docCtx;
  }

  public String keyToTitle(String key, DocumentContext schemaJson) {
    var keyParts = keyToKeyParts(key);

    var jsonPath =
        keyParts.stream().map(k -> ".properties." + k.replaceAll("\\[\\d+\\]$", ".items"))
            .collect(Collectors.joining());

    return schemaJson.read("$" + jsonPath + ".title", String.class);
  }

  public String keyToType(String key, DocumentContext schemaJson) {
    var keyParts = keyToKeyParts(key);

    var jsonPath =
        keyParts.stream().map(k -> ".properties." + k.replaceAll("\\[\\d+\\]$", ".items"))
            .collect(Collectors.joining());

    return schemaJson.read("$" + jsonPath + ".type", String.class);
  }

  private List<String> keyToKeyParts(String key) {
    if (key.isBlank()) return new ArrayList<>();
    return new ArrayList<>(Arrays.asList(key.split("\\.")));
  }

  public String keyToItemTitle(String key, DocumentContext schemaJson) {
    var keyParts = keyToKeyParts(key);
    if (!key.matches(".*\\[\\d+\\]$")) {
      keyParts.remove(keyParts.size() - 1);
    }

    var jsonPath =
        keyParts.stream().map(k -> ".properties." + k.replaceAll("\\[\\d+\\]$", ".items"))
            .collect(Collectors.joining());

    return schemaJson.read("$" + jsonPath + ".title", String.class);
  }

  public String keyToItemType(String key, DocumentContext schemaJson) {
    var keyParts = keyToKeyParts(key);
    if (!key.matches(".*\\[\\d+\\]$")) {
      keyParts.remove(keyParts.size() - 1);
    }

    var jsonPath =
        keyParts.stream().map(k -> ".properties." + k.replaceAll("\\[\\d+\\]$", ".items"))
            .collect(Collectors.joining());

    return schemaJson.read("$" + jsonPath + ".type", String.class);
  }

  public boolean isKeyInArray(String key) {
    return key.matches(".*\\[\\d+\\]$") || key.matches(".*\\[\\d+\\]\\..*");
  }

  public int getKeyArrayIndex(String key) {
    if (!isKeyInArray(key)) return -1;

    var m = Pattern.compile("\\[(\\d+)\\]").matcher(key);
    int index = -1;
    while (m.find()) {
      index = Integer.valueOf(m.group(1));
    }
    return index;
  }

  public String keyToArrayKey(String key, DocumentContext schemaJson) {
    if (!key.matches(".*\\[\\d+\\]$") && !key.matches(".*\\[\\d+\\]\\..*")) return null;

    var keyParts = keyToKeyParts(key);
    while (!keyParts.get(keyParts.size() - 1).matches(".*\\[\\d+\\]$")) {
      keyParts.remove(keyParts.size() - 1);
    }
    var lastPart = keyParts.get(keyParts.size() - 1);
    lastPart = lastPart.replaceAll("\\[\\d+\\]$", "");
    keyParts.remove(keyParts.size() - 1);
    keyParts.add(lastPart);

    return keyParts.stream().collect(Collectors.joining("."));
  }

  public String keyToArrayTitle(String key, DocumentContext schemaJson) {
    if (!key.matches(".*\\[\\d+\\]$") && !key.matches(".*\\[\\d+\\]\\..*")) return null;

    var keyParts = keyToKeyParts(keyToArrayKey(key, schemaJson));
    var jsonPath =
        keyParts.stream().map(k -> ".properties." + k.replaceAll("\\[\\d+\\]$", ".items"))
            .collect(Collectors.joining());

    return schemaJson.read("$" + jsonPath + ".title", String.class);
  }

  public String keyToParentKey(String key, DocumentContext schemaJson) {
    var keyParts = keyToKeyParts(key);

    if (isKeyInArray(key) && !key.matches(".*\\[\\d+\\]$")) {
      keyParts.remove(keyParts.size() - 1);
    }
    keyParts.remove(keyParts.size() - 1);

    return keyParts.stream().collect(Collectors.joining("."));
  }

  public String keyToParentTitle(String key, DocumentContext schemaJson) {
    var keyParts = keyToKeyParts(keyToParentKey(key, schemaJson));

    var jsonPath =
        keyParts.stream().map(k -> ".properties." + k.replaceAll("\\[\\d+\\]$", ".items"))
            .collect(Collectors.joining());

    return schemaJson.read("$" + jsonPath + ".title", String.class);
  }

  public String keyToParentType(String key, DocumentContext schemaJson) {
    var keyParts = keyToKeyParts(key);

    keyParts.remove(keyParts.size() - 1);
    var jsonPath =
        keyParts.stream().map(k -> ".properties." + k.replaceAll("\\[\\d+\\]$", ".items"))
            .collect(Collectors.joining());

    return schemaJson.read("$" + jsonPath + ".type", String.class);
  }

  public String keyToRootKey(String key, DocumentContext schemaJson) {
    var keyParts = keyToKeyParts(key);
    var rootKey = keyParts.get(0);
    return keyParts.size() <= 1 ? "" : rootKey.replaceAll("\\[\\d+\\]$", "");
  }

  public String keyToRootTitle(String key, DocumentContext schemaJson) {
    var keyParts = keyToKeyParts(key);
    if (keyParts.size() <= 1) return schemaJson.read("$.title", String.class);

    var jsonPath =
        keyParts.subList(0, 1).stream().map(k -> ".properties." + k.replaceAll("\\[\\d+\\]$", ""
        // ".items"
        )).collect(Collectors.joining());

    return schemaJson.read("$" + jsonPath + ".title", String.class);
  }

  public LinkedHashMap<Object, String> keyToEnumToNames(String key, DocumentContext schemaJson) {
    var enumToNames = new LinkedHashMap<Object, String>();
    var keyParts = keyToKeyParts(key);

    var jsonPath =
        keyParts.stream().map(k -> ".properties." + k.replaceAll("\\[\\d+\\]$", ".items"))
            .collect(Collectors.joining());

    var enumList = schemaJson.read("$" + jsonPath + ".enum", List.class);
    var enumNames = schemaJson.read("$" + jsonPath + ".enumNames", List.class);
    if (enumList != null) {
      for (int i = 0; i < enumList.size(); i++) {
        if (enumNames != null && enumNames.size() > i) {
          enumToNames.put(enumList.get(i), enumNames.get(i).toString());
        } else {
          enumToNames.put(enumList.get(i), null);
        }
      }
    }

    return enumToNames;
  }

}
