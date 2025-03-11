package com.github.wnameless.spring.boot.up.jsf;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public final class BasicJsfDefaultEnum implements JsfDefaultEnum {

  String documentTypeName;
  String enumPath;
  JsfDefaultEnumType enumType;
  List<String> enumNames = new ArrayList<>();
  List<String> enumStrings = new ArrayList<>();
  List<Integer> enumIntegers = new ArrayList<>();
  List<Double> enumNumbers = new ArrayList<>();
  List<Boolean> enumBooleans = new ArrayList<>();

}
