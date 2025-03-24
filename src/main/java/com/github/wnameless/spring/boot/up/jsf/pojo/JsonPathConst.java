package com.github.wnameless.spring.boot.up.jsf.pojo;

import static lombok.AccessLevel.PRIVATE;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import net.sf.rubycollect4j.Ruby;

@Data
@FieldDefaults(level = PRIVATE)
public class JsonPathConst {

  String jsonPath;

  String stringConst;

  Integer integerConst;

  Double numberConst;

  Boolean booleanConst;

  public Object getConst() {
    return Ruby.Array.of(stringConst, integerConst, numberConst, booleanConst).compact().first();
  }

}
