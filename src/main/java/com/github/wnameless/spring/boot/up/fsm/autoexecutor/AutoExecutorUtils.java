package com.github.wnameless.spring.boot.up.fsm.autoexecutor;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import com.github.wnameless.spring.boot.up.fsm.Phase;
import com.github.wnameless.spring.boot.up.fsm.PhaseProvider;
import com.github.wnameless.spring.boot.up.fsm.State;
import com.github.wnameless.spring.boot.up.fsm.Trigger;
import com.github.wnameless.spring.boot.up.fsm.TriggerType;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AutoExecutorUtils {

  public <E extends PhaseProvider<E, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID> List<? extends Trigger> getAlwaysTriggers(
      Phase<E, S, T, ID> phase) {
    return phase.getAllTriggers().stream().filter(t -> t.getTriggerType() == TriggerType.ALWAYS)
        .toList();
  }

  @SuppressWarnings("unchecked")
  public <E extends PhaseProvider<E, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID> Set<S> getAlwaysTriggerStates(
      PhaseProvider<E, S, T, ID> phaseProvider) {
    var alwaysTriggerStates = new LinkedHashSet<S>();

    var phase = phaseProvider.getPhase();
    var stateMachineConfig = phase.getStateMachineConfigInternal();
    var states = phase.getAllStates();
    var alwaysTriggers = getAlwaysTriggers(phase);
    for (var s : states) {
      var r = stateMachineConfig.getRepresentation(s);
      if (r == null) continue;

      alwaysTriggers.stream().filter(t -> {
        try {
          return r.canHandle((T) t);
        } catch (Exception e) {
          return false;
        }
      }).findAny().ifPresent(t -> {
        alwaysTriggerStates.add(s);
      });
    }

    return alwaysTriggerStates;
  }

}
