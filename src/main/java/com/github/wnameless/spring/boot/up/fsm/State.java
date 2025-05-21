package com.github.wnameless.spring.boot.up.fsm;

import java.util.List;

public interface State<T extends Trigger, ID> {

  String getName();

  String getDisplayName();

  default StateType getStateType() {
    var forms = getForms();
    return forms == null || forms.isEmpty() ? StateType.SIMPLE : StateType.FORM;
  }

  List<StateForm<T, ID>> getForms();

}
