package com.github.wnameless.spring.boot.up.fsm;

import java.util.List;
import java.util.stream.Collectors;
import com.github.oxo42.stateless4j.StateMachineConfig;

/**
 * Centralized configuration for all IRB application state forms. This class consolidates form
 * declarations, triggers, and permission conditions.
 */
public interface StateFormConfigurator<P extends Phase<E, S, T, ID>, E extends PhaseProvider<E, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID> {

  /**
   * Get all form configurations.
   */
  List<StateFormConfiguration<P, E, S, T, ID>> getConfigurations();

  /**
   * Get form configurations for a specific state.
   */
  default List<StateFormConfiguration<P, E, S, T, ID>> getConfigurationsForState(S state) {
    return getConfigurations().stream()
        .filter(config -> config.getApplicableStates().contains(state))
        .collect(Collectors.toList());
  }

  /**
   * Get state forms for a specific state. This method is called from IrbApplicationState with the
   * phase context.
   */
  default List<StateForm<T, ID>> getStateFormsForState(S state) {
    return getConfigurationsForState(state).stream().map(StateFormConfiguration::getStateForm)
        .collect(Collectors.toList());
  }

  /**
   * Apply state form configurations to the state machine config. This method is called from
   * IrbApplicationPhase.
   */
  default void applyStateFormConfigurations(StateMachineConfig<S, T> config, P phase) {
    // Apply form configurations to each state
    for (S state : phase.getAllStates()) {
      List<StateFormConfiguration<P, E, S, T, ID>> stateConfigs = getConfigurationsForState(state);

      for (StateFormConfiguration<P, E, S, T, ID> formConfig : stateConfigs) {
        StateForm<T, ID> form = formConfig.getStateForm();

        // Apply view permission if present
        if (formConfig.getViewPermissionCondition(phase) != null
            && form.viewableTriggerStock() != null) {
          T viewTrigger = form.viewableTriggerStock().get();
          if (viewTrigger != null) {
            config.configure(state).permitInternalIf(viewTrigger,
                formConfig.getViewPermissionCondition(phase), () -> {});
          }
        }

        // Apply edit permission if present
        if (formConfig.getEditPermissionCondition(phase) != null
            && form.editableTriggerStock() != null) {
          T editTrigger = form.editableTriggerStock().get();
          if (editTrigger != null) {
            config.configure(state).permitInternalIf(editTrigger,
                formConfig.getEditPermissionCondition(phase), () -> {});
          }
        }

        // Apply entire view permission if present
        if (formConfig.getEntireViewPermissionCondition(phase) != null
            && form.entireViewableTriggerStock() != null) {
          T entireViewTrigger = form.entireViewableTriggerStock().get();
          if (entireViewTrigger != null) {
            config.configure(state).permitInternalIf(entireViewTrigger,
                formConfig.getEntireViewPermissionCondition(phase), () -> {});
          }
        }
      }
    }
  }

}
