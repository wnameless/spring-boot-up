package com.github.wnameless.spring.boot.up.fsm;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AbstractPhase<E extends PhaseAware<E, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID>
    implements Phase<E, S, T, ID> {

  protected final Supplier<E> entitySupplier;
  protected final Supplier<StateRecord<S, T, ID>> stateRecordSupplier;
  protected final Consumer<StateRecord<S, T, ID>> stateRecordConsumer;

  public AbstractPhase(Supplier<E> entitySupplier,
      Supplier<StateRecord<S, T, ID>> stateRecordSupplier,
      Consumer<StateRecord<S, T, ID>> stateRecordConsumer) {
    this.entitySupplier = entitySupplier;
    this.stateRecordSupplier = stateRecordSupplier;
    this.stateRecordConsumer = stateRecordConsumer;
  }

  @Override
  public E getEntity() {
    return entitySupplier.get();
  }

  @Override
  public StateRecord<S, T, ID> getStateRecord() {
    return stateRecordSupplier.get();
  }

  @Override
  public void setStateRecord(StateRecord<S, T, ID> stateRecord) {
    stateRecordConsumer.accept(stateRecord);
  }

  @Override
  public S getCurrentState() {
    return getStateRecord().getState();
  }

}
