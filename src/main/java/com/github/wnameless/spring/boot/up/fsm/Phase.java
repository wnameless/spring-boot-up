package com.github.wnameless.spring.boot.up.fsm;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.delegates.Trace;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.permission.PermittedUser;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControllable;

public interface Phase<E extends PhaseProvider<E, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID>
    extends AccessControllable, StateMachineInitializable<S, T> {

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

  default Trace<S, T> getTrace() {
    return new Trace<>() {

      @Override
      public void trigger(T trigger) {}

      @Override
      public void transition(T trigger, S source, S destination) {
        StateRecord<S, T, ID> stateRecord = getStateRecord();

        List<StateAuditTrail<S, T, ID>> auditTrails = stateRecord.getAuditTrails();
        if (auditTrails == null) auditTrails = new ArrayList<>();

        var username =
            SpringBootUp.findBean(PermittedUser.class).map(PermittedUser::getUsername).orElse(null);
        var stateAuditTrail = new StateAuditTrail<S, T, ID>(source, trigger, destination, username);

        auditTrails.add(stateAuditTrail);
        stateRecord.setAuditTrails(auditTrails);

        setStateRecord(stateRecord);
      }

    };
  }

  default StateMachine<S, T> getStateMachine() {
    S state = getStateRecord().getState();
    if (state == null) state = initialState();
    StateMachine<S, T> stateMachine = new StateMachine<>(state, getStateMachineConfig());
    Trace<S, T> trace = getTrace();
    if (trace != null) stateMachine.setTrace(trace);
    return stateMachine;
  }

  default Function<T, Boolean> verifyTrigger() {
    return t -> true;
  }

}
