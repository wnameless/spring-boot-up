package com.github.wnameless.spring.boot.up.fsm;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControlAware;

public interface Phase<E extends PhaseAware<E, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID>
    extends AccessControlAware, StateMachineInitializable<S, T> {

  E getEntity();

  List<S> getAllStates();

  List<T> getAllTriggers();

  default List<T> getExternalTriggers() {
    return getStateMachine().getPermittedTriggers().stream()
        .filter(t -> t.getTriggerType() == TriggerType.SIMPLE).toList();
  }

  default List<T> getInternalTriggers() {
    return getStateMachine().getPermittedTriggers().stream()
        .filter(t -> t.getTriggerType() == TriggerType.INTERNAL).toList();
  }

  default List<T> getAlwaysTriggers() {
    return getStateMachine().getPermittedTriggers().stream()
        .filter(t -> t.getTriggerType() == TriggerType.ALWAYS).toList();
  }

  default T getTrigger(String triggerName) {
    return getAllTriggers().stream().filter(t -> Objects.equals(triggerName, t.getName()))
        .findFirst().get();
  }

  StateRecord<S, T, ID> getStateRecord();

  void setStateRecord(StateRecord<S, T, ID> stateRecord);

  S initialState();

  default StateMachine<S, T> getStateMachine() {
    S state = getStateRecord().getState();
    if (state == null) state = initialState();
    return new StateMachine<>(state, getStateMachineConfig());
  }

  default Function<T, Boolean> verifyTrigger() {
    return t -> true;
  }

}
