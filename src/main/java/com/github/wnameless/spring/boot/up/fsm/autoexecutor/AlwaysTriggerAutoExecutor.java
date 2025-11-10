package com.github.wnameless.spring.boot.up.fsm.autoexecutor;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.StreamSupport;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.fsm.PhaseProvider;
import com.github.wnameless.spring.boot.up.fsm.State;
import com.github.wnameless.spring.boot.up.notification.NotifiableStateMachine;
import com.github.wnameless.spring.boot.up.notification.NotificationStrategy;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import lombok.SneakyThrows;

public interface AlwaysTriggerAutoExecutor extends SchedulingConfigurer {

  Set<Class<? extends PhaseProvider<?, ?, ?, ?>>> getPhaseProviderTypes();

  @SneakyThrows
  @SuppressWarnings({"unchecked", "rawtypes"})
  default List<? extends PhaseProvider> findAllPhaseProvidersContainingAlwaysTriggerState(
      Class<? extends PhaseProvider> phaseProviderType) {
    var pp = phaseProviderType.getDeclaredConstructor().newInstance();
    var repoOpt = SpringBootUp.findGenericBean(QuerydslPredicateExecutor.class, pp.getClass());
    if (repoOpt.isEmpty()) return Collections.emptyList();

    var repo = repoOpt.get();
    PathBuilder<?> entityPath =
        new PathBuilder<>(phaseProviderType, phaseProviderType.getSimpleName());
    Set<State> alwaysTriggerStates = AutoExecutorUtils.getAlwaysTriggerStates(pp);
    var stateNames = alwaysTriggerStates.stream().map(State::getName).toList();
    var q = Expressions.stringPath(entityPath, "stateRecord.state").in(stateNames);
    var fsmItems = repo.findAll(q);
    var fsmItemList = StreamSupport.stream(fsmItems.spliterator(), false).toList();
    return fsmItemList;
  }

  @SuppressWarnings("rawtypes")
  Set<State> getAlwaysNotificationStates(Class<? extends PhaseProvider> phaseProviderType);

  @SneakyThrows
  @SuppressWarnings({"unchecked", "rawtypes"})
  default List<? extends PhaseProvider> findAllPhaseProvidersContainingAlwaysNotificationAdvice(
      Class<? extends PhaseProvider> phaseProviderType) {
    var pp = phaseProviderType.getDeclaredConstructor().newInstance();
    var repoOpt = SpringBootUp.findGenericBean(QuerydslPredicateExecutor.class, pp.getClass());
    if (repoOpt.isEmpty()) return Collections.emptyList();

    Set<State> states = getAlwaysNotificationStates(phaseProviderType);
    if (!states.isEmpty()) {
      var repo = repoOpt.get();
      PathBuilder<?> entityPath =
          new PathBuilder<>(phaseProviderType, phaseProviderType.getSimpleName());
      var stateNames = states.stream().map(State::getName).toList();
      var q = Expressions.stringPath(entityPath, "stateRecord.state").in(stateNames);
      var fsmItems = repo.findAll(q);
      var fsmItemList = StreamSupport.stream(fsmItems.spliterator(), false).toList();
      return fsmItemList;
    }

    return Collections.emptyList();
  }

  @SuppressWarnings("unchecked")
  default void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(Executors.newSingleThreadScheduledExecutor());
    taskRegistrar.addFixedDelayTask(() -> {
      for (var phaseProviderType : getPhaseProviderTypes()) {
        for (var fsmItem : findAllPhaseProvidersContainingAlwaysTriggerState(phaseProviderType)) {
          var phase = fsmItem.getPhase();
          var stateRecord = fsmItem.getStateRecord();
          for (var alwaysTrigger : AutoExecutorUtils.getAlwaysTriggers(phase)) {
            var stateMachine = new StateMachine<>(
                stateRecord != null ? stateRecord.getState() : phase.initialState(),
                phase.getStateMachineConfigInternal());
            if (stateMachine.canFire(alwaysTrigger)) {
              stateMachine.fire(alwaysTrigger);
            }
          }
        }

        var strategies = SpringBootUp.getBeansOfType(NotificationStrategy.class).values();
        for (var fsmItem : findAllPhaseProvidersContainingAlwaysNotificationAdvice(
            phaseProviderType)) {
          var phase = fsmItem.getPhase();
          for (var strategy : strategies) {
            var expectedClass = strategy.getNotifiableStateMachineType();
            var actualClass = phase.getClass();

            // Check exact match OR if actual is subclass/proxy of expected
            // IMPORTANT: isAssignableFrom() is required to handle Spring CGLIB proxies
            if (expectedClass.equals(actualClass) || expectedClass.isAssignableFrom(actualClass)) {
              if (phase instanceof NotifiableStateMachine nsm)
                strategy.applyAlwaysNotificationStrategy(nsm);
            }
          }
        }
      }
    }, getFixedDelay());
  }

  default Duration getFixedDelay() {
    return Duration.ofMinutes(10); // 10 minute delay after task completion
  }

}
