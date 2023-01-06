package com.github.wnameless.spring.boot.up.fsm;

import java.util.HashMap;
import java.util.Map;
import com.github.oxo42.stateless4j.StateMachine;
import lombok.Data;

@Data
public class StateRecord<S extends State<T>, T extends Trigger, ID> {

  private S state;

  private Map<String, Map<String, ID>> formDataTable = new HashMap<>();

  public StateRecord() {}

  public StateRecord(S state) {
    this.state = state;
  }

  public boolean hasForm() {
    return !state.getForms().isEmpty();
  }

  public boolean hasViewableForm(StateMachine<S, T> sm) {
    return state.getForms().stream().anyMatch(f -> sm.canFire(f.viewableTriggerStock().get()));
  }

}
