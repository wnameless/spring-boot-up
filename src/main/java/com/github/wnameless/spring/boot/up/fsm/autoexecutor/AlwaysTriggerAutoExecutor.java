package com.github.wnameless.spring.boot.up.fsm.autoexecutor;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.fsm.PhaseProvider;
import com.github.wnameless.spring.boot.up.fsm.State;
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

  @SuppressWarnings("unchecked")
  default void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.setPoolSize(1);
    taskScheduler.setThreadNamePrefix("always-trigger-auto-executor-pool-");
    taskScheduler.initialize(); // Important: initialize the scheduler!
    taskRegistrar.addCronTask(() -> {
      for (var phaseProviderType : getPhaseProviderTypes()) {
        for (var fsmItem : findAllPhaseProvidersContainingAlwaysTriggerState(phaseProviderType)) {
          var phase = fsmItem.getPhase();
          for (var alwaysTrigger : AutoExecutorUtils.getAlwaysTriggers(phase)) {
            var stateMachine = new StateMachine<>(fsmItem.getStateRecord().getState(),
                phase.getStateMachineConfigInternal());
            if (stateMachine.canFire(alwaysTrigger)) {
              stateMachine.fire(alwaysTrigger);
            }
          }
        }
      }
    }, getCronExpression());
  }

  default String getCronExpression() {
    return "0 */1 * * * *"; // Every 1 minute
  }

}
