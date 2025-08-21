package com.github.wnameless.spring.boot.up.notification;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import org.springframework.core.GenericTypeResolver;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.fsm.Phase;
import com.github.wnameless.spring.boot.up.fsm.State;
import com.github.wnameless.spring.boot.up.fsm.Trigger;
import com.github.wnameless.spring.boot.up.fsm.TriggerType;
import com.github.wnameless.spring.boot.up.web.RestfulItem;
import lombok.SneakyThrows;

public interface QuickConfigurableNotificationStrategy< //
    CN extends NotificationConfigurationProvider, //
    NC extends NotificationCallback<NS, ID>, //
    NT extends NotificationTarget<NS, NR, M, ID>, //
    NS extends NotificationSource<ID>, //
    NR extends NotificationReceiver<M>, //
    M, //
    SM extends NotifiableStateMachine<SM, S, T> & Phase<? extends RestfulItem<ID>, S, T, ID>, //
    S extends Enum<S> & State<T, ID>, //
    T extends Enum<T> & Trigger, //
    ID> extends ConfigurableNotificationStrategy<CN, NC, NT, NS, NR, M, SM, S, T, ID> {

  Set<NR> roleNamesToReceivers(List<String> roleNames, CN configurableNotification,
      SM stateMachine);

  default NotificationPlan<S, T> convertToNotificationPlan(CN configurableNotification,
      SM stateMachine) {
    var onAdvice = NotificationAdvice
        .valueOf(configurableNotification.getNotificationConfiguration().getOnAdvice());

    NotificationRule<S, T> rule = new NotificationRule<>();
    var targetState = configurableNotification.getNotificationConfiguration().getTargetState();
    rule.setState(getStateMachineStates().stream().filter(s -> s.name().equals(targetState))
        .findFirst().get());

    var notificationInterval =
        configurableNotification.getNotificationConfiguration().getNotificationInterval();
    if (notificationInterval != null && notificationInterval > 0) {
      rule.setAlwaysActionInterval(Duration.ofHours(notificationInterval));
    }

    String title = getFormattedMessage(
        configurableNotification.getNotificationConfiguration().getMessageTitle(),
        configurableNotification.getNotificationConfiguration().getTitleProperties(), stateMachine);
    String content = getFormattedMessage(
        configurableNotification.getNotificationConfiguration().getMessageContent(),
        configurableNotification.getNotificationConfiguration().getContentProperties(),
        stateMachine);
    String actionPath = "#";
    if (configurableNotification.getNotificationConfiguration().getActionPath() != null) {
      switch (configurableNotification.getNotificationConfiguration().getActionPath()) {
        case "INDEX":
          actionPath = stateMachine.getEntity().getIndexPath();
          break;
        case "SHOW":
          actionPath = stateMachine.getEntity().getShowPath();
          break;
        case "EDIT":
          actionPath = stateMachine.getEntity().getEditPath();
          break;
        case "NEW":
          actionPath = stateMachine.getEntity().getNewPath();
          break;
      }
    }

    var receivers =
        roleNamesToReceivers(configurableNotification.getNotificationConfiguration().getMessageTo(),
            configurableNotification, stateMachine);
    NS notificationSource;
    if (onAdvice != NotificationAdvice.ALWAYS) {
      notificationSource =
          getNotificationService().findOrCreateNotificationSource(title, content, actionPath);
    } else {
      notificationSource = null;
    }
    switch (onAdvice) {
      case ENTRY_FROM:
        var initTrigger = configurableNotification.getNotificationConfiguration().getInitTrigger();
        rule.setTrigger(getStateMachineTriggers().stream().filter(t -> t.name().equals(initTrigger))
            .findFirst().get());
        rule.setAdvice(NotificationAdvice.ENTRY_FROM);
        rule.setEntryAction((arg1, arg2) -> {
          getNotificationService().createNotificationTarget(notificationSource, receivers);
        });
        break;
      case ENTRY:
        rule.setAdvice(NotificationAdvice.ENTRY);
        rule.setEntryAction((arg1, arg2) -> {
          getNotificationService().createNotificationTarget(notificationSource, receivers);
        });
        break;
      case EXIT:
        rule.setAdvice(NotificationAdvice.EXIT);
        rule.setExitAction(arg1 -> {
          getNotificationService().createNotificationTarget(notificationSource, receivers);
        });
        break;
      case ALWAYS:
        if (rule.getAlwaysActionInterval() == null) return null;

        NS alwaysNotificationSource = getNotificationService().findOrCreateNotificationSource(title,
            content, actionPath, stateMachine.getEntity().getId());
        rule.setAdvice(NotificationAdvice.ALWAYS);
        rule.setAlwaysAction(() -> {
          var alwaysTriggerReceivers =
              getNotificationService().getAlwaysTriggerNotificationReceivers(
                  rule.getAlwaysActionInterval(), alwaysNotificationSource, receivers);
          getNotificationService().createNotificationTarget(alwaysNotificationSource,
              alwaysTriggerReceivers);
        });
        break;
    }

    // Callback
    if (notificationSource == null) return rule;

    NC callback = newNotificationCallback();
    callback.setStateMachineEntityId((ID) stateMachine.getEntity().getId());
    callback.setNotificationSource(notificationSource);
    if (List.of(NotificationAdvice.values()).stream().map(String::valueOf).toList()
        .contains(configurableNotification.getNotificationConfiguration().getMessageHook())) {
      var messageHook = NotificationAdvice
          .valueOf(configurableNotification.getNotificationConfiguration().getMessageHook());
      var messageHookState =
          configurableNotification.getNotificationConfiguration().getMessageHookState();
      callback.setAdvice(messageHook);
      callback.setState(getStateMachineStates().stream()
          .filter(s -> s.name().equals(messageHookState)).findFirst().get());
      if (messageHook == NotificationAdvice.ENTRY_FROM) {
        var messageHookTrigger = getStateMachineTriggers().stream()
            .filter(t -> t.name().equals(
                configurableNotification.getNotificationConfiguration().getMessageHookTrigger()))
            .findFirst().get();
        callback.setTrigger(messageHookTrigger);
      }
      getNotificationService().findOrCreateNotificationCallback(callback);
    }

    return rule;
  }

  @SuppressWarnings("unchecked")
  @SneakyThrows
  default NC newNotificationCallback() {
    var genericTypeResolver = GenericTypeResolver.resolveTypeArguments(this.getClass(),
        QuickConfigurableNotificationStrategy.class);
    return (NC) genericTypeResolver[1].getDeclaredConstructor().newInstance();
  }

  String getFormattedMessage(String message, List<String> properties, SM stateMachine);

  @SuppressWarnings("unchecked")
  default Class<SM> getNotifiableStateMachineType() {
    var genericTypeResolver = GenericTypeResolver.resolveTypeArguments(this.getClass(),
        QuickConfigurableNotificationStrategy.class);
    return (Class<SM>) genericTypeResolver[6];
  }

  @SuppressWarnings("unchecked")
  default NotificationService<NC, NT, NS, NR, M, ID> getNotificationService() {
    return (NotificationService<NC, NT, NS, NR, M, ID>) SpringBootUp
        .getBean(NotificationService.class);
  }

  @SuppressWarnings("unchecked")
  default List<T> getStateMachineTriggers() {
    var genericTypeResolver = GenericTypeResolver.resolveTypeArguments(this.getClass(),
        QuickConfigurableNotificationStrategy.class);
    Class<? extends Enum<T>> enumClass =
        (Class<? extends Enum<T>>) genericTypeResolver[8].asSubclass(Enum.class);
    Enum<T>[] constants = enumClass.getEnumConstants();

    return List.of(constants).stream().map(c -> (T) c)
        .filter(t -> TriggerType.SIMPLE.equals(t.getTriggerType())).toList();
  }

  @SuppressWarnings("unchecked")
  default List<S> getStateMachineStates() {
    var genericTypeResolver = GenericTypeResolver.resolveTypeArguments(this.getClass(),
        QuickConfigurableNotificationStrategy.class);
    Class<? extends Enum<S>> enumClass =
        (Class<? extends Enum<S>>) genericTypeResolver[7].asSubclass(Enum.class);
    Enum<S>[] constants = enumClass.getEnumConstants();

    return List.of(constants).stream().map(c -> (S) c).toList();
  }

}
