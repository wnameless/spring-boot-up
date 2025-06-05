package com.github.wnameless.spring.boot.up.fsm;

import java.util.function.Function;
import com.github.wnameless.spring.boot.up.web.WebActionAlertHelper;

public interface StateCondition<PP extends PhaseProvider<PP, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID>
    extends Function<PP, Boolean> {

  default String getAlertType() {
    return WebActionAlertHelper.INFO_NAME;
  }

}
