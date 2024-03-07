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
  private final Class<? extends JsfPOJO<?>> jsfPojoType;
  private final Class<? extends CrudRepository<?, ID>> jsfRepositoryType;

  private final Supplier<String> formBranchStock;
  private final Supplier<T> viewableTriggerStock;
  private final Supplier<T> editableTriggerStock;

  private final Supplier<T> entireViewableTriggerStock;
  private final Supplier<T> entireEditableTriggerStock;

  public static <T extends Trigger, ID> StateForm<T, ID> of(Supplier<String> formTypeStock,
      Supplier<String> formBranchStock, Supplier<T> viewableTriggerStock,
      Supplier<T> editableTriggerStock) {
    return new StateForm<>(formTypeStock, formBranchStock, viewableTriggerStock,
        editableTriggerStock);
  }

  public static <T extends Trigger, ID> StateForm<T, ID> of(Supplier<String> formTypeStock,
      Supplier<String> formBranchStock, Supplier<T> viewableTriggerStock,
      Supplier<T> editableTriggerStock, Supplier<T> entireViewableTriggerStock,
      Supplier<T> entireEditableTriggerStock) {
    return new StateForm<>(formTypeStock, formBranchStock, viewableTriggerStock,
        editableTriggerStock, entireViewableTriggerStock, entireEditableTriggerStock);
  }

  public StateForm(Supplier<String> formTypeStock, Supplier<String> formBranchStock,
      Supplier<T> viewableTriggerStock, Supplier<T> editableTriggerStock) {
    this.formTypeStock = formTypeStock;
    isJsfPojo = false;
    jsfPojoType = null;
    jsfRepositoryType = null;
    this.formBranchStock = formBranchStock;
    this.viewableTriggerStock = viewableTriggerStock;
    this.editableTriggerStock = editableTriggerStock;
    this.entireViewableTriggerStock = () -> null;
    this.entireEditableTriggerStock = () -> null;
  }

  public StateForm(Supplier<String> formTypeStock, Supplier<String> formBranchStock,
      Supplier<T> viewableTriggerStock, Supplier<T> editableTriggerStock,
      Supplier<T> entireViewableTriggerStock, Supplier<T> entireEditableTriggerStock) {
    this.formTypeStock = formTypeStock;
    isJsfPojo = false;
    jsfPojoType = null;
    jsfRepositoryType = null;
    this.formBranchStock = formBranchStock;
    this.viewableTriggerStock = viewableTriggerStock;
    this.editableTriggerStock = editableTriggerStock;
    this.entireViewableTriggerStock = entireViewableTriggerStock;
    this.entireEditableTriggerStock = entireEditableTriggerStock;
  }

  public static <T extends Trigger, ID> StateForm<T, ID> of(Class<? extends JsfPOJO<?>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType, Supplier<String> formBranchStock,
      Supplier<T> viewableTriggerStock, Supplier<T> editableTriggerStock) {
    return new StateForm<>(formType, jsfRepositoryType, formBranchStock, viewableTriggerStock,
        editableTriggerStock);
  }

  public StateForm(Class<? extends JsfPOJO<?>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType, Supplier<String> formBranchStock,
      Supplier<T> viewableTriggerStock, Supplier<T> editableTriggerStock) {
    this.formTypeStock = () -> formType.getSimpleName();
    isJsfPojo = true;
    jsfPojoType = formType;
    this.jsfRepositoryType = jsfRepositoryType;
    this.formBranchStock = formBranchStock;
    this.viewableTriggerStock = viewableTriggerStock;
    this.editableTriggerStock = editableTriggerStock;
    this.entireViewableTriggerStock = () -> null;
    this.entireEditableTriggerStock = () -> null;
  }

  public static <T extends Trigger, ID> StateForm<T, ID> of(String formType) {
    return new StateForm<>(formType);
  }

  public StateForm(String formType) {
    this.formTypeStock = () -> formType;
    isJsfPojo = false;
    jsfPojoType = null;
    jsfRepositoryType = null;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> null;
    this.editableTriggerStock = () -> null;
    this.entireViewableTriggerStock = () -> null;
    this.entireEditableTriggerStock = () -> null;
  }

  public static <T extends Trigger, ID> StateForm<T, ID> of(Class<? extends JsfPOJO<?>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType) {
    return new StateForm<>(formType, jsfRepositoryType);
  }

  public StateForm(Class<? extends JsfPOJO<?>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType) {
    this.formTypeStock = () -> formType.getSimpleName();
    isJsfPojo = true;
    jsfPojoType = formType;
    this.jsfRepositoryType = jsfRepositoryType;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> null;
    this.editableTriggerStock = () -> null;
    this.entireViewableTriggerStock = () -> null;
    this.entireEditableTriggerStock = () -> null;
  }

  public static <T extends Trigger, ID> StateForm<T, ID> of(String formType, T viewableTrigger) {
    return new StateForm<>(formType, viewableTrigger);
  }

  public StateForm(String formType, T viewableTrigger) {
    this.formTypeStock = () -> formType;
    isJsfPojo = false;
    jsfPojoType = null;
    jsfRepositoryType = null;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> viewableTrigger;
    this.editableTriggerStock = () -> null;
    this.entireViewableTriggerStock = () -> null;
    this.entireEditableTriggerStock = () -> null;
  }

  public static <T extends Trigger, ID> StateForm<T, ID> of(Class<? extends JsfPOJO<?>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType, T viewableTrigger) {
    return new StateForm<>(formType, jsfRepositoryType, viewableTrigger);
  }

  public StateForm(Class<? extends JsfPOJO<?>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType, T viewableTrigger) {
    this.formTypeStock = () -> formType.getSimpleName();
    isJsfPojo = true;
    jsfPojoType = formType;
    this.jsfRepositoryType = jsfRepositoryType;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> viewableTrigger;
    this.editableTriggerStock = () -> null;
    this.entireViewableTriggerStock = () -> null;
    this.entireEditableTriggerStock = () -> null;
  }

  public static <T extends Trigger, ID> StateForm<T, ID> of(String formType, T viewableTrigger,
      T editableTrigger) {
    return new StateForm<>(formType, viewableTrigger, editableTrigger);
  }

  public StateForm(String formType, T viewableTrigger, T editableTrigger) {
    this.formTypeStock = () -> formType;
    isJsfPojo = false;
    jsfPojoType = null;
    jsfRepositoryType = null;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> viewableTrigger;
    this.editableTriggerStock = () -> editableTrigger;
    this.entireViewableTriggerStock = () -> null;
    this.entireEditableTriggerStock = () -> null;
  }

  public static <T extends Trigger, ID> StateForm<T, ID> of(Class<? extends JsfPOJO<?>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType, T viewableTrigger,
      T editableTrigger) {
    return new StateForm<>(formType, jsfRepositoryType, viewableTrigger, editableTrigger);
  }

  public StateForm(Class<? extends JsfPOJO<?>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType, T viewableTrigger,
      T editableTrigger) {
    this.formTypeStock = () -> formType.getSimpleName();
    isJsfPojo = true;
    jsfPojoType = formType;
    this.jsfRepositoryType = jsfRepositoryType;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> viewableTrigger;
    this.editableTriggerStock = () -> editableTrigger;
    this.entireViewableTriggerStock = () -> null;
    this.entireEditableTriggerStock = () -> null;
  }

  public static <T extends Trigger, ID> StateForm<T, ID> of(Class<? extends JsfPOJO<?>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType, T viewableTrigger,
      T editableTrigger, T entireViewableTrigger, T entireEditableTrigger) {
    return new StateForm<>(formType, jsfRepositoryType, viewableTrigger, editableTrigger,
        entireViewableTrigger, entireEditableTrigger);
  }

  public StateForm(Class<? extends JsfPOJO<?>> formType,
      Class<? extends CrudRepository<?, ID>> jsfRepositoryType, T viewableTrigger,
      T editableTrigger, T entireViewableTrigger, T entireEditableTrigger) {
    this.formTypeStock = () -> formType.getSimpleName();
    isJsfPojo = true;
    jsfPojoType = formType;
    this.jsfRepositoryType = jsfRepositoryType;
    this.formBranchStock = () -> JsfConfig.getDefaultBranchName();
    this.viewableTriggerStock = () -> viewableTrigger;
    this.editableTriggerStock = () -> editableTrigger;
    this.entireViewableTriggerStock = () -> entireViewableTrigger;
    this.entireEditableTriggerStock = () -> entireEditableTrigger;
  }

}
