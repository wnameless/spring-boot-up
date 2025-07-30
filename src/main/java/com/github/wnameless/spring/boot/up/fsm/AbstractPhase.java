package com.github.wnameless.spring.boot.up.fsm;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.springframework.core.GenericTypeResolver;
import com.github.oxo42.stateless4j.StateMachineConfig;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.notification.NotifiableStateMachine;
import com.github.wnameless.spring.boot.up.notification.NotificationStrategy;

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
    return Optional.ofNullable(stateRecordSupplier.get()).orElseGet(() -> {
      var stateRecord = new StateRecord<>(initialState());
      stateRecordConsumer.accept(stateRecord);
      return stateRecord;
    });
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
      // Because NotifiableStateMachine#stateMachineConfigStrategy has been overwritten, we need to
      // add it back here
      if (this instanceof NotifiableStateMachine nsm) {
        var strategies = SpringBootUp.getBeansOfType(NotificationStrategy.class).values();
        for (var strategy : strategies) {
          if (strategy.getNotifiableStateMachineType().equals(this.getClass())) {
            strategy.applyNotificationStrategy(config, nsm.getNotifiableStateMachine());
          }
        }
      }

      var stateFormConfigOpt = SpringBootUp.findGenericBean(StateFormConfigurator.class,
          this.getClass(), genericTypeResolver[0], genericTypeResolver[1], genericTypeResolver[2],
          genericTypeResolver[3]);
      stateFormConfigOpt
          .ifPresent(stateFormConfig -> stateFormConfig.applyStateFormConfigurations(config, this));

      return config;
    };
  }

}
