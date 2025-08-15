package com.github.wnameless.spring.boot.up.actioncode;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.function.BiFunction;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.repository.CrudRepository;
import org.springframework.web.servlet.ModelAndView;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import lombok.SneakyThrows;

public interface SingularActionCodeService<AC extends SingularActionCode<A>, A extends Enum<?>, ID> {

  SingularActionCodeRepository<AC, A, ID> getActionCodeRepository();

  @SneakyThrows
  @SuppressWarnings("unchecked")
  default AC newActionCode() {
    var genericTypeResolver = GenericTypeResolver
        .resolveTypeArguments(getActionCodeRepository().getClass(), CrudRepository.class);
    return (AC) genericTypeResolver[0].getDeclaredConstructor().newInstance();
  }

  A getActionEnum(String actionName);

  default String getActionEnumDisplay(A actionEnum) {
    return SpringBootUp.getMessage("sbu.actioncode."
        + actionEnum.getDeclaringClass().getSimpleName() + "." + actionEnum.name(),
        new Object[] {actionEnum}, null);
  }

  default BiFunction<ModelAndView, A, ModelAndView> actionCodeRequest() {
    return (mav, action) -> {
      var rgOpt = getActionCodeRepository().findByActionAndExpiredAtGreaterThan(action,
          LocalDateTime.now(Clock.systemUTC()));
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
      var acOpt = getActionCodeRepository().findByActionAndExpiredAtGreaterThan(action,
          LocalDateTime.now(Clock.systemUTC()));
      if (acOpt.isPresent() && acOpt.get().isValid()) {
        var ac = acOpt.get();
        if (ac.isExpired()) {
          ac.setCode(getRandomCode());
          ac.setExpiredAt(LocalDateTime.now(Clock.systemUTC()).plusDays(30));
          getActionCodeRepository().save(ac);
        }
        mav.addObject(ActionCodeAttributes.CODE, ac.getCode());
      } else {
        AC actionCode = newActionCode();
        actionCode.setAction(action);
        actionCode.setCode(getRandomCode());
        actionCode.setExpiredAt(LocalDateTime.now(Clock.systemUTC()).plusDays(30));
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
