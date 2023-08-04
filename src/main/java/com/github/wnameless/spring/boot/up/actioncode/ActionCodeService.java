package com.github.wnameless.spring.boot.up.actioncode;

import java.util.Optional;
import java.util.Random;

public interface ActionCodeService<AC extends ActionCode<A>, A extends Enum<?>, ID> {

  Optional<AC> findByActionAndCode(A action, String code);

  void deleteByActionCode(AC actionCode);

  A getActionEnum(String actionName);

  default String getRandomCode() {
    int letterNumberZero = 48;
    int letterAlphabetLowerZ = 122;
    int codeLength = 32;
    Random random = new Random();

    String generatedCode = random.ints(letterNumberZero, letterAlphabetLowerZ + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(codeLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();

    return generatedCode;
  }

}
