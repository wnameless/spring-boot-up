package com.github.wnameless.spring.boot.up.fsm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.springframework.core.ResolvableType;
import org.springframework.data.repository.CrudRepository;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.JsfConfig;
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

  public List<StateAuditTrail<S, T, ID>> getReversedAuditTrails() {
    var auditTrailsCopy = new ArrayList<StateAuditTrail<S, T, ID>>(auditTrails);
    Collections.reverse(auditTrailsCopy);
    return auditTrailsCopy;
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

  public boolean hasViewableForm(PhaseProvider<?, S, T, ID> pp) {
    return hasViewableForm(pp.getPhase().getStateMachine());
  }

  public boolean hasViewableForm(StateMachine<S, T> sm) {
    return state.getForms().stream().anyMatch(f -> sm.canFire(f.viewableTriggerStock().get()));
  }

  public List<String> getViewableForms(PhaseProvider<?, S, T, ID> pp) {
    StateMachine<S, T> sm = pp.getPhase().getStateMachine();
    var viewableFormTypes = new ArrayList<String>();

    var stateForms =
        state.getForms().stream().filter(f -> sm.canFire(f.viewableTriggerStock().get())).toList();
    stateForms.forEach(sf -> {
      var formType = sf.formTypeStock().get();
      var formBranch = sf.formBranchStrategy().apply(pp);
      if (formDataTable.keySet().contains(formType)) {
        if (formDataTable.get(formType) instanceof Map<String, ID> formBranchIdMap) {
          if (formBranchIdMap.get(formBranch) != null) {
            viewableFormTypes.add(formType);
          } else if (sm.canFire(sf.editableTriggerStock().get())) {
            viewableFormTypes.add(formType);
          }
        }
      } else if (sm.canFire(sf.editableTriggerStock().get())) {
        viewableFormTypes.add(formType);
      }
    });

    return viewableFormTypes;
  }

  public boolean hasEntireViewableForms(PhaseProvider<?, S, T, ID> pp) {
    return hasEntireViewableForms(pp.getPhase().getStateMachine());
  }

  public boolean hasEntireViewableForms(StateMachine<S, T> sm) {
    return state.getForms().stream()
        .anyMatch(f -> sm.canFire(f.entireViewableTriggerStock().get()));
  }

  public Map<String, Map<String, ID>> getEntireViewableForms(PhaseProvider<?, S, T, ID> pp) {
    StateMachine<S, T> sm = pp.getPhase().getStateMachine();
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

  public Optional<ID> findStateFormId(Class<?> formType, String formBranch) {
    return findStateFormId(formType.getSimpleName(), formBranch);
  }

  public Optional<ID> findStateFormIdOnDefaultBranch(String formType) {
    return findStateFormId(formType, JsfConfig.getDefaultBranchName());
  }

  public Optional<ID> findStateFormIdOnDefaultBranch(Class<?> formType) {
    return findStateFormIdOnDefaultBranch(formType.getSimpleName());
  }

  public List<ID> findAllStateFormIds(String formType, Collection<String> formBranches) {
    return getFormDataTable().getOrDefault(formType, new LinkedHashMap<>()).entrySet().stream()
        .filter(e -> formBranches.contains(e.getKey())).map(Entry::getValue).toList();
  }

  public List<ID> findAllStateFormIds(Class<?> formType, Collection<String> formBranches) {
    return getFormDataTable().getOrDefault(formType.getSimpleName(), new LinkedHashMap<>())
        .entrySet().stream().filter(e -> formBranches.contains(e.getKey())).map(Entry::getValue)
        .toList();
  }

  public List<ID> findAllStateFormIdsOnDefaultBranch(String formType) {
    return findAllStateFormIds(formType, List.of(JsfConfig.getDefaultBranchName()));
  }

  public List<ID> findAllStateFormIdsOnDefaultBranch(Class<?> formType) {
    return findAllStateFormIds(formType.getSimpleName(), List.of(JsfConfig.getDefaultBranchName()));
  }

  public <F> Optional<F> findStateForm(Class<F> formType) {
    return findStateForm(formType, JsfConfig.getDefaultBranchName());
  }

  public <F> Optional<F> findStateFormOnDefaultBranch(Class<F> formType) {
    return findStateForm(formType);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public <F> Optional<F> findStateForm(Class<F> formType, String formBranch) {
    var idOpt = findStateFormId(formType, formBranch);
    if (idOpt.isEmpty()) return Optional.empty();

    var repositories = SpringBootUp.getBeansOfType(CrudRepository.class);
    Optional<CrudRepository> formRepoOpt = repositories.values().stream().filter(repo -> {
      ResolvableType[] generics =
          ResolvableType.forClass(repo.getClass()).as(CrudRepository.class).getGenerics();
      return generics.length > 0 && generics[0].resolve() == formType;
    }).findFirst();

    return formRepoOpt.flatMap(repo -> repo.findById(idOpt.get()));
  }

  public <F> boolean hasStateForm(Class<F> formType) {
    return findStateFormId(formType.getSimpleName(), JsfConfig.getDefaultBranchName()).isPresent();
  }

  public <F> boolean hasStateFormOnDefaultBranch(Class<F> formType) {
    return hasStateForm(formType);
  }

  public <F> boolean hasStateForm(Class<F> formType, String formBranch) {
    return findStateFormId(formType.getSimpleName(), formBranch).isPresent();
  }

}
