package com.github.wnameless.spring.boot.up.fsm;

import java.util.List;
import com.github.oxo42.stateless4j.delegates.FuncBoolean;

/**
 * Configuration interface for centralizing state form declarations and permissions. This interface
 * defines the contract for configuring state forms with their associated triggers and permission
 * conditions.
 *
 * @param <T> the trigger type
 * @param <ID> the ID type
 */
public interface StateFormConfiguration<P extends Phase<E, S, T, ID>, E extends PhaseProvider<E, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID> {

  /**
   * Returns the StateForm instance for this configuration.
   * 
   * @return the StateForm instance
   */
  StateForm<T, ID> getStateForm();

  /**
   * Returns the permission condition for viewing this form. The FuncBoolean should return true if
   * the current user has permission to view the form.
   * 
   * @return FuncBoolean that evaluates view permission
   */
  FuncBoolean getViewPermissionCondition(P phase);

  /**
   * Returns the permission condition for editing this form. The FuncBoolean should return true if
   * the current user has permission to edit the form.
   * 
   * @return FuncBoolean that evaluates edit permission
   */
  FuncBoolean getEditPermissionCondition(P phase);

  /**
   * Returns the permission condition for viewing the entire form (if applicable). This is used for
   * forms that have both individual and entire view modes.
   * 
   * @return FuncBoolean that evaluates entire view permission, or null if not applicable
   */
  default FuncBoolean getEntireViewPermissionCondition(P phase) {
    return null;
  }

  /**
   * Returns the permission condition for editing the entire form (if applicable). This is used for
   * forms that have both individual and entire edit modes.
   * 
   * @return FuncBoolean that evaluates entire edit permission, or null if not applicable
   */
  default FuncBoolean getEntireEditPermissionCondition(P phase) {
    return null;
  }

  /**
   * Returns the list of states where this form configuration applies.
   * 
   * @return list of applicable states
   */
  List<? extends com.github.wnameless.spring.boot.up.fsm.State<T, ID>> getApplicableStates();

  /**
   * Returns the form name for identification purposes.
   * 
   * @return the form name
   */
  default String getFormName() {
    return getStateForm().formTypeStock().get();
  }

}
