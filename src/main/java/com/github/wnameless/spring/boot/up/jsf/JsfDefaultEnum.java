package com.github.wnameless.spring.boot.up.jsf;

import java.util.List;
import com.github.wnameless.spring.boot.up.jsf.pojo.JsonPathConst;

public interface JsfDefaultEnum {

  String getDocumentTypeName();

  String getEnumPath();

  List<JsonPathConst> getIfConditions();

  JsfDefaultEnumType getEnumType();

  List<String> getEnumNames();

  List<String> getEnumStrings();

  List<Integer> getEnumIntegers();

  List<Double> getEnumNumbers();

  List<Boolean> getEnumBooleans();

  default List<?> getEnum() {
    return switch (getEnumType()) {
      case STRING -> getEnumStrings();
      case INTEGER -> getEnumIntegers();
      case NUMBER -> getEnumNumbers();
      case BOOLEAN -> getEnumBooleans();
    };
  }

}
