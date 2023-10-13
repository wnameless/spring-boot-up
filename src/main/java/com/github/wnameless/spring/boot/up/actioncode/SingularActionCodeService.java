package com.github.wnameless.spring.boot.up.actioncode;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.function.BiFunction;
import org.springframework.web.servlet.ModelAndView;
import com.github.wnameless.spring.boot.up.SpringBootUp;

public interface SingularActionCodeService<AC extends SingularActionCode<A>, A extends Enum<?>, ID> {

  SingularActionCodeRepository<AC, A, ID> getActionCodeRepository();

  AC newActionCode();

  A getActionEnum(String actionName);

  default String getActionEnumDisplay(A actionEnum) {
    return SpringBootUp.getMessage("sbu.actioncode."
        + actionEnum.getDeclaringClass().getSimpleName() + "." + actionEnum.name(),
        new Object[] {actionEnum}, null);
  }

  default BiFunction<ModelAndView, A, ModelAndView> actionCodeRequest() {
    return (mav, action) -> {
      var rgOpt = getActionCodeRepository().findByAction(action);
      if (rgOpt.isPresent()) {
        var rg = rgOpt.get();
        if (rg.isValid()) {
          mav.addObject(ActionCodeAttributes.CODE, rgOpt.get().getCode());
        }
      }
      return mav;
    };
  }

  default BiFunction<ModelAndView, A, ModelAndView> actionCodeGeneration() {
    return (mav, action) -> {
      var acOpt = getActionCodeRepository().findByAction(action);
      if (acOpt.isPresent() && acOpt.get().isValid()) {
        var ac = acOpt.get();
        if (ac.isExpired()) {
          ac.setCode(getRandomCode());
          ac.setExpiredAt(LocalDateTime.now().plusDays(30));
          getActionCodeRepository().save(ac);
        }
        mav.addObject(ActionCodeAttributes.CODE, ac.getCode());
      } else {
        AC actionCode = newActionCode();
        actionCode.setAction(action);
        actionCode.setCode(getRandomCode());
        actionCode.setExpiredAt(LocalDateTime.now().plusDays(30));
        getActionCodeRepository().save(actionCode);
        mav.addObject(ActionCodeAttributes.CODE, actionCode.getCode());
      }
      return mav;
    };
  }

  BiFunction<ModelAndView, AC, ModelAndView> actionCodeExecution();

  default BiFunction<ModelAndView, AC, ModelAndView> actionCodeDeletion() {
    return (mav, actionCode) -> {
      getActionCodeRepository().delete(actionCode);
      return mav;
    };
  }

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
