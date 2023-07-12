package com.github.wnameless.spring.boot.up.fsm;

import java.util.function.Supplier;
import org.springframework.data.repository.CrudRepository;
import com.github.wnameless.spring.boot.up.jsf.JsfConfig;
import com.github.wnameless.spring.boot.up.jsf.JsfPOJO;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
@Data
public class StateForm<T extends Trigger, ID> {

  private final Supplier<String> formTypeStock;
  private final boolean isJsfPojo;
  private final Class<? extends JsfPOJO<?, ID>> jsfPojoType;

  private final Class<? extends CrudRepository<?, ID>> jsfRepositoryType;

  private final Supplier<String> formBranchStock;
  private final Supplier<T> viewableTriggerStock;
  private final Supplier<T> editableTriggerStock;

  public StateForm(Supplier<String> formTypeStock, Supplier<String> formBranchStock,
      Supplier<T> viewableTriggerStock, Supplier<T> editableTriggerStock) {
    this.formTypeStock = formTypeStock;
    isJsfPojo = false;
    jsfPojoType = null;
    jsfRepositoryType = null;
    this.formBranchStock = formBranchStock;
    this.viewableTriggerStock = viewableTriggerStock;
    this.editableTriggerStock = editableTriggerStock;
  }

  public StateForm(Class<? extends JsfPOJO<?, ID>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType, Supplier<String> formBranchStock,
      Supplier<T> viewableTriggerStock, Supplier<T> editableTriggerStock) {
    this.formTypeStock = () -> formType.getSimpleName();
    isJsfPojo = true;
    jsfPojoType = formType;
    this.jsfRepositoryType = jsfRepositoryType;
    this.formBranchStock = formBranchStock;
    this.viewableTriggerStock = viewableTriggerStock;
    this.editableTriggerStock = editableTriggerStock;
  }

  public StateForm(String formType) {
    this.formTypeStock = () -> formType;
    isJsfPojo = false;
    jsfPojoType = null;
    jsfRepositoryType = null;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> null;
    this.editableTriggerStock = () -> null;
  }

  public StateForm(Class<? extends JsfPOJO<?, ID>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType) {
    this.formTypeStock = () -> formType.getSimpleName();
    isJsfPojo = true;
    jsfPojoType = formType;
    this.jsfRepositoryType = jsfRepositoryType;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> null;
    this.editableTriggerStock = () -> null;
  }

  public StateForm(String formType, T viewableTrigger) {
    this.formTypeStock = () -> formType;
    isJsfPojo = false;
    jsfPojoType = null;
    jsfRepositoryType = null;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> viewableTrigger;
    this.editableTriggerStock = () -> null;
  }

  public StateForm(Class<? extends JsfPOJO<?, ID>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType, T viewableTrigger) {
    this.formTypeStock = () -> formType.getSimpleName();
    isJsfPojo = true;
    jsfPojoType = formType;
    this.jsfRepositoryType = jsfRepositoryType;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> viewableTrigger;
    this.editableTriggerStock = () -> null;
  }


  public StateForm(String formType, T viewableTrigger, T editableTrigger) {
    this.formTypeStock = () -> formType;
    isJsfPojo = false;
    jsfPojoType = null;
    jsfRepositoryType = null;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> viewableTrigger;
    this.editableTriggerStock = () -> editableTrigger;
  }

  public StateForm(Class<? extends JsfPOJO<?, ID>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType, T viewableTrigger,
      T editableTrigger) {
    this.formTypeStock = () -> formType.getSimpleName();
    isJsfPojo = true;
    jsfPojoType = formType;
    this.jsfRepositoryType = jsfRepositoryType;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> viewableTrigger;
    this.editableTriggerStock = () -> editableTrigger;
  }

}
