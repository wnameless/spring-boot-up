package com.github.wnameless.spring.boot.up.fsm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateRepresentation;
import com.github.oxo42.stateless4j.delegates.Trace;
import com.github.oxo42.stateless4j.triggers.TriggerBehaviour;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.permission.PermittedUser;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControllable;
import com.github.wnameless.spring.boot.up.permission.role.Role;
import lombok.SneakyThrows;
import net.sf.rubycollect4j.Ruby;

public interface Phase<E extends PhaseProvider<E, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID>
    extends AccessControllable, StateMachineInitializable<S, T> {

  E getEntity();

  List<S> getAllStates();

  List<T> getAllTriggers();

  @SuppressWarnings("unchecked")
  @SneakyThrows
  default List<ActiveTrigger<T>> getActiveTriggers() {
    List<ActiveTrigger<T>> activeTriggers = new ArrayList<>();

    S state = getStateMachine().getState();
    StateRepresentation<S, T> rep = getStateMachine().configuration().getRepresentation(state);
    List<T> permittedTriggers = getStateMachine().getPermittedTriggers();
    Method method = StateRepresentation.class.getDeclaredMethod("getTriggerBehaviours");
    method.setAccessible(true);

    Map<T, List<TriggerBehaviour<S, T>>> triggerBehaviours;
    if (rep == null) {
      triggerBehaviours = Map.of();
    } else {
      triggerBehaviours = (Map<T, List<TriggerBehaviour<S, T>>>) method.invoke(rep);
    }
    for (T trigger : getAllTriggers()) {
      if (Ruby.Object.isBlank(triggerBehaviours.get(trigger))) continue;
      if (trigger.getTriggerType() != TriggerType.SIMPLE) continue;

      boolean disable = !permittedTriggers.contains(trigger);
      var phaseName = this.getClass().getSimpleName();
      if (disable) {
        var msgKey = Ruby.Array
            .of("sbu.fsm.message.prohibited", phaseName, state.getName(), trigger.getName())
            .join(".");
        var message = SpringBootUp.getMessage(msgKey, new Object[] {}, null);

        var rolesIncludeKey = Ruby.Array.of("sbu.fsm.message.prohibited", phaseName,
            state.getName(), trigger.getName(), "roles_include").join(".");
        var rolesInclude = SpringBootUp.getMessage(rolesIncludeKey, new Object[] {}, null);
        if (rolesInclude instanceof String roles) {
          var roleList = List.of(roles.split(",")).stream().map(String::strip).toList();
          var phaseRoleAwareOpt = SpringBootUp.findBean(PhaseRoleAware.class);
          if (phaseRoleAwareOpt.isPresent()) {
            var phaseRoles = phaseRoleAwareOpt.get().getRoles();
            if (Ruby.Array.of(phaseRoles).map(Role::getRoleName).intersection(roleList).isEmpty()) {
              message = null;
            }
          }
        }

        var at = new ActiveTrigger<>(trigger);
        at.setDisable(disable);
        at.setMessage(message);
        activeTriggers.add(at);
      } else {
        var msgKey = Ruby.Array
            .of("sbu.fsm.message.permitted", phaseName, state.getName(), trigger.getName())
            .join(".");
        var message = SpringBootUp.getMessage(msgKey, new Object[] {}, null);
        var at = new ActiveTrigger<>(trigger);
        at.setDisable(disable);
        at.setMessage(message);
        activeTriggers.add(at);
      }
    }

    return activeTriggers;
  }

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
