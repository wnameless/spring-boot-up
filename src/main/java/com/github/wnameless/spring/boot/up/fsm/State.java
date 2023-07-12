package com.github.wnameless.spring.boot.up.fsm;

import java.util.List;

public interface State<T extends Trigger, ID> {

  String getName();

  String getDisplayName();

  StateType getStateType();

  List<StateForm<T, ID>> getForms();

}
