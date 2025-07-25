package com.github.wnameless.spring.boot.up.fsm;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.core.GenericTypeResolver;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.wnameless.spring.boot.up.SpringBootUp;

public abstract class AbstractPhase<E extends PhaseProvider<E, S, T, ID>, S extends State<T, ID>, T extends Trigger, ID>
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
    return Optional.ofNullable(stateRecordSupplier.get()).orElse(new StateRecord<>(initialState()));
  }

  @Override
  public void setStateRecord(StateRecord<S, T, ID> stateRecord) {
    stateRecordConsumer.accept(stateRecord);
  }

  @Override
  public S getCurrentState() {
    return getStateRecord().getState();
  }

  @SuppressWarnings("unchecked")
  @Override
  public Function<StateMachineConfig<S, T>, StateMachineConfig<S, T>> stateMachineConfigStrategy() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), AbstractPhase.class);

    return config -> {
      var stateFormConfigOpt = SpringBootUp.findGenericBean(StateFormConfigurator.class,
          this.getClass(), genericTypeResolver[0], genericTypeResolver[1], genericTypeResolver[2],
          genericTypeResolver[3]);
      stateFormConfigOpt
          .ifPresent(stateFormConfig -> stateFormConfig.applyStateFormConfigurations(config, this));

      return config;
    };
  }

}
