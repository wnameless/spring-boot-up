package com.github.wnameless.spring.boot.up.jsf;

import java.util.List;

public interface JsfDefaultEnum {

  String getDocumentTypeName();

  String getEnumPath();

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
