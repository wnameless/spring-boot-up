package com.github.wnameless.spring.boot.up.fsm;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import com.github.oxo42.stateless4j.StateMachine;
import lombok.Data;

@Data
public class StateRecord<S extends State<T, ID>, T extends Trigger, ID> {

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

  public ID putStateFormId(String formType, String formBranch, ID formId) {
    formDataTable.putIfAbsent(formType, new LinkedHashMap<>());
    return formDataTable.get(formType).put(formBranch, formId);
  }

  public Optional<ID> findStateFormId(String formType, String formBranch) {
    return Optional
        .ofNullable(formDataTable.getOrDefault(formType, new LinkedHashMap<>()).get(formBranch));
  }

  public List<ID> findAllStateFormIds(String formType, Collection<String> formBranches) {
    return getFormDataTable().getOrDefault(formType, new LinkedHashMap<>()).entrySet().stream()
        .filter(e -> formBranches.contains(e.getKey())).map(Entry::getValue).toList();
  }

}
