package com.github.wnameless.spring.boot.up.fsm;

import java.util.function.Supplier;
import com.github.wnameless.spring.boot.up.jsf.JsfConfig;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class StateForm<T extends Trigger> {

  private final Supplier<String> formTypeStock;
  private final Supplier<String> formBranchStock;
  private final Supplier<T> viewableTriggerStock;
  private final Supplier<T> editableTriggerStock;

  public StateForm(Supplier<String> formTypeStock, Supplier<String> formBranchStock,
      Supplier<T> viewableTriggerStock, Supplier<T> editableTriggerStock) {
    this.formTypeStock = formTypeStock;
    this.formBranchStock = formBranchStock;
    this.viewableTriggerStock = viewableTriggerStock;
    this.editableTriggerStock = editableTriggerStock;
  }

  public StateForm(String formType) {
    this.formTypeStock = () -> formType;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> null;
    this.editableTriggerStock = () -> null;
  }

  public StateForm(String formType, T viewableTrigger) {
    this.formTypeStock = () -> formType;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> viewableTrigger;
    this.editableTriggerStock = () -> null;
  }

  public StateForm(String formType, T viewableTrigger, T editableTrigger) {
    this.formTypeStock = () -> formType;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> viewableTrigger;
    this.editableTriggerStock = () -> editableTrigger;
  }

}
