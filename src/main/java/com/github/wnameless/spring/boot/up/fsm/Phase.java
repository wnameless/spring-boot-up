package com.github.wnameless.spring.boot.up.fsm;

import java.util.List;
import java.util.function.Function;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterDeleteFromMongo;
import com.github.wnameless.spring.boot.up.jsf.service.JsfService;

public interface Phase<SELF extends Phase<SELF, S, T, ID>, S extends State<T>, T extends Trigger, ID> {

  @SuppressWarnings("unchecked")
  @AfterDeleteFromMongo
  default void cleanUpByFormDataTable() {
    var ids = getSelf().getStateRecord().getFormDataTable().values().stream()
        .flatMap(m -> m.values().stream()).toList();
    SpringBootUp.getBean(JsfService.class).getJsfDataRepository().deleteAllById(ids);
  }

  SELF getSelf();

  default List<T> getExternalTriggers() {
    return getStateMachine().getPermittedTriggers().stream()
        .filter(t -> t.getTriggerType() != TriggerType.INTERNAL).toList();
  }

  default List<T> getInternalTriggers() {
    return getStateMachine().getPermittedTriggers().stream()
        .filter(t -> t.getTriggerType() == TriggerType.INTERNAL).toList();
  }

  T getTrigger(String triggerName);

  StateRecord<S, T, ID> getStateRecord();

  void setStateRecord(StateRecord<S, T, ID> stateRecord);

  S initialState();

  StateMachineConfig<S, T> getStateMachineConfig();

  default StateMachine<S, T> getStateMachine() {
    S state = getStateRecord().getState();
    if (state == null) state = initialState();
    return new StateMachine<>(state, getStateMachineConfig());
  }

  default Function<T, Boolean> verifyTrigger() {
    return (t) -> true;
  }

}
