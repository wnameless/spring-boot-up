package com.github.wnameless.spring.boot.up.fsm;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterDeleteFromMongo;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.BeforeConvertToMongo;
import com.github.wnameless.spring.boot.up.jsf.service.JsfService;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControllable;
import com.github.wnameless.spring.boot.up.permission.resource.ForwardingAccessControllable;
import com.github.wnameless.spring.boot.up.web.IdProvider;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PrePersist;

public interface PhaseProvider<E extends PhaseProvider<E, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID>
    extends ForwardingAccessControllable, IdProvider<ID> {

  Class<? extends AbstractPhase<E, S, T, ID>> getPhaseType();

  default Phase<E, S, T, ID> getPhase() {
    @SuppressWarnings("unchecked")
    Supplier<E> arg0 = () -> (E) this;
    Supplier<StateRecord<S, T, ID>> arg1 = () -> getStateRecord();
    Consumer<StateRecord<S, T, ID>> arg2 = sr -> setStateRecord(sr);
    try {
      return getPhaseType().getConstructor(Supplier.class, Supplier.class, Consumer.class)
          .newInstance(arg0, arg1, arg2);
    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      throw new RuntimeException(e);
    }
  }

  StateRecord<S, T, ID> getStateRecord();

  void setStateRecord(StateRecord<S, T, ID> stateRecord);

  @PrePersist // JPA
  @BeforeConvertToMongo // MongoDB
  default void initStateRecord() {
    if (getStateRecord() == null) {
      setStateRecord(new StateRecord<>(getPhase().initialState()));
    }
  }

  @SuppressWarnings("unchecked")
  @PostRemove // JPA
  @AfterDeleteFromMongo // MongoDB
  default void cleanUpByFormDataTable() {
    List<StateForm<T, ID>> stateForms =
        getPhase().getAllStates().stream().flatMap(s -> s.getForms().stream()).toList();
    Map<String, Map<String, ID>> formDataTable = getPhase().getStateRecord().getFormDataTable();

    for (String formType : formDataTable.keySet()) {
      var sfOpt =
          stateForms.stream().filter(sf -> formType.equals(sf.formTypeStock().get())).findFirst();
      if (sfOpt.isEmpty()) continue;

      var sf = sfOpt.get();
      if (sf.isJsfPojo()) {
        var repo = SpringBootUp.getBean(sf.jsfRepositoryType());
        repo.deleteAllById(formDataTable.get(formType).values());
      } else {
        var ids = formDataTable.get(formType).values();
        SpringBootUp.getBean(JsfService.class).getJsfDataRepository().deleteAllById(ids);
      }
    }
  }

  @Override
  default AccessControllable accessControllable() {
    return getPhase();
  }

  default boolean hasEntireViewableForms() {
    if (getPhase().getStateRecord() == null) return false;
    return getPhase().getStateRecord().hasEntireViewableForms(this);
  }

  default Map<String, Map<String, ID>> getEntireViewableForms() {
    if (getPhase().getStateRecord() == null) return Map.of();
    return getPhase().getStateRecord().getEntireViewableForms(this);
  }

  default boolean hasViewableForm() {
    if (getPhase().getStateRecord() == null) return false;
    return getPhase().getStateRecord().hasViewableForm(this);
  }

  default List<String> getViewableForms() {
    if (getPhase().getStateRecord() == null) return List.of();
    return getPhase().getStateRecord().getViewableForms(this);
  }

}
