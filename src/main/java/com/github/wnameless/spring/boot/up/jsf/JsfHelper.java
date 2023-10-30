package com.github.wnameless.spring.boot.up.jsf;

import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class JsfHelper {

  public Map<String, Object> ifThenEnum(String ifProp, String ifValue, String thenProp,
      List<String> thenEnum) {
    return Map.of(//
        "if", Map.of( //
            "properties", Map.of( //
                ifProp, Map.of( //
                    "const", ifValue))), //
        "then", Map.of(//
            "properties", Map.of( //
                thenProp, Map.of( //
                    "enum", thenEnum))));
  }

  public Map<String, Object> ifThenEnumAndNames(String ifProp, String ifValue, String thenProp,
      List<String> thenEnum, List<String> thenEnumNames) {
    return Map.of(//
        "if", Map.of( //
            "properties", Map.of( //
                ifProp, Map.of( //
                    "const", ifValue))), //
        "then", Map.of( //
            "properties", Map.of( //
                thenProp, Map.of( //
                    "enum", thenEnum, //
                    "enumNames", thenEnumNames))));
  }

}
