package com.github.wnameless.spring.boot.up.jsf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public final class JsonSchemaFormUtils {

  private JsonSchemaFormUtils() {}

  // @SuppressWarnings("unchecked")
  public static Map<String, Object> defaultSchema() {
    return JsonCoreFactory.INSTANCE.readJson(
        "{\"title\": \"Default Form\",\"type\": \"object\",\"properties\": {\"warning\": {\"type\": \"string\",\"title\": \"Please update this schema ASAP.\",\"default\": \"Hurry up!\"}}}")
        .asObject().toMap();
    // try {
    // return JacksonObjectMapperFactory.INSTANCE.getObjectMapper().readValue(
    // "{\"title\": \"Default Form\",\"type\": \"object\",\"properties\":
    // {\"warning\": {\"type\": \"string\",\"title\": \"Please update this
    // schema ASAP.\",\"default\": \"Hurry up!\"}}}",
    // Map.class);
    // } catch (JsonProcessingException e) {
    // throw new RuntimeException(e);
    // }
  }

  public static Map<String, Object> defaultUiSchema() {
    return new LinkedHashMap<>();
  }

  public static Map<String, Object> defaultFormData() {
    return new LinkedHashMap<>();
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static Map<String, String> propertyTitles(JsonSchemaForm rjsf) {
    Map<String, String> propertyTitles = new LinkedHashMap<>();

    Map<String, ?> schema = rjsf.getSchema();
    Map<String, Object> schemaProperties = (Map<String, Object>) schema.get("properties");
    Iterator<Entry<String, Object>> fields = schemaProperties.entrySet().iterator();
    while (fields.hasNext()) {
      Entry<String, Object> f = fields.next();
      String title = ((Map) f.getValue()).get("title") == null ? ""
          : ((Map) f.getValue()).get("title").toString();
      propertyTitles.put(f.getKey(), title);
    }

    return propertyTitles;
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Object> mapSetter(Map<String, Object> map, String keyDotNotation,
      Object value) {
    List<String> keys = new ArrayList<>(Arrays.asList(keyDotNotation.split(Pattern.quote("."))));
    Map<String, Object> tempMap = map;
    while (keys.size() > 1) {
      tempMap = (Map<String, Object>) tempMap.get(keys.remove(0));
    }
    tempMap.put(keys.remove(0), value);
    return map;
  }

}
