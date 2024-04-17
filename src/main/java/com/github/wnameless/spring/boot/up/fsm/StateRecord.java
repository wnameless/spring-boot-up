package com.github.wnameless.spring.boot.up.fsm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.permission.PermittedUser;
import lombok.Data;

@Data
public class StateRecord<S extends State<T, ID>, T extends Trigger, ID> {

  private S state;

  // Map<FormType, Map<FormBranch, ID>>
  private Map<String, Map<String, ID>> formDataTable = new HashMap<>();

  private List<StateAuditTrail<S, T, ID>> auditTrails = new ArrayList<>();

  public StateRecord() {}

  public StateRecord(S state) {
    this.state = state;
  }

  public void setStateWithAuditTrail(S state) {
    var username =
        SpringBootUp.findBean(PermittedUser.class).map(PermittedUser::getUsername).orElse(null);
    if (auditTrails == null) auditTrails = new ArrayList<>();
    var auditTrail = new StateAuditTrail<>(this.state, null, state, username);
    auditTrails.add(auditTrail);

    this.state = state;
  }

  public boolean hasForm() {
    return !state.getForms().isEmpty();
  }

  public boolean hasViewableForm(StateMachine<S, T> sm) {
    return state.getForms().stream().anyMatch(f -> sm.canFire(f.viewableTriggerStock().get()));
  }

  public boolean hasEntireViewableForms(StateMachine<S, T> sm) {
    return state.getForms().stream()
        .anyMatch(f -> sm.canFire(f.entireViewableTriggerStock().get()));
  }

  public Map<String, Map<String, ID>> getEntireViewableForms(StateMachine<S, T> sm) {
    var dataTableCopy = new LinkedHashMap<>(formDataTable);

    var formTypes =
        state.getForms().stream().filter(f -> sm.canFire(f.entireViewableTriggerStock().get()))
            .map(sf -> sf.formTypeStock().get()).toList();
    var removableKeys =
        dataTableCopy.keySet().stream().filter(k -> !formTypes.contains(k)).toList();
    removableKeys.forEach(rk -> dataTableCopy.remove(rk));

    return dataTableCopy;
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
