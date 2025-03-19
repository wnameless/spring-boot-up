package com.github.wnameless.spring.boot.up.jsf.util;

import static lombok.AccessLevel.PRIVATE;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class JsfPropertyDetail {

  String key;
  int nestedLevel = 0;

  String rootKey = null;
  String rootTitle = null;

  String parentKey = null;
  String parentTitle = null;
  String parentType = null;

  boolean inArray = false;
  String arrayKey = null;
  int arrayIndex = -1;
  String arrayTitle = null;
  String itemTitle = null;
  String itemType = null;

  @ToString.Exclude
  Map<Object, String> enumToNames = new LinkedHashMap<>();

  String title;
  String type;
  Object value;

}
