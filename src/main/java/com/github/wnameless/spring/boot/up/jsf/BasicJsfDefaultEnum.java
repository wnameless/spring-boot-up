package com.github.wnameless.spring.boot.up.jsf;

import static lombok.AccessLevel.PRIVATE;
import java.util.ArrayList;
import java.util.List;
import com.github.wnameless.spring.boot.up.jsf.pojo.JsonPathConst;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public final class BasicJsfDefaultEnum implements JsfDefaultEnum {

  String documentTypeName;
  String enumPath;
  List<JsonPathConst> ifConditions = new ArrayList<>();
  JsfDefaultEnumType enumType;
  List<String> enumNames = new ArrayList<>();
  List<String> enumStrings = new ArrayList<>();
  List<Integer> enumIntegers = new ArrayList<>();
  List<Double> enumNumbers = new ArrayList<>();
  List<Boolean> enumBooleans = new ArrayList<>();

}
