package com.github.wnameless.spring.boot.up.fsm;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.data.mongodb.interceptor.annotation.AfterDeleteFromMongo;
import com.github.wnameless.spring.boot.up.jsf.service.JsfService;

public interface PhaseAware<E extends PhaseAware<E, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID> {

  Class<? extends AbstractPhase<E, S, T, ID>> getPhaseType();

  default Phase<E, S, T, ID> getPhase() {
    Supplier<E> arg0 = () -> getPhaseAwareEntity();
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

  E getPhaseAwareEntity();

  StateRecord<S, T, ID> getStateRecord();

  void setStateRecord(StateRecord<S, T, ID> stateRecord);

  @SuppressWarnings("unchecked")
  @AfterDeleteFromMongo
  default void cleanUpByFormDataTable() {
    List<StateForm<T, ID>> stateForms = getStateRecord().getState().getForms();
    Map<String, Map<String, ID>> formDataTable = getStateRecord().getFormDataTable();

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

}
