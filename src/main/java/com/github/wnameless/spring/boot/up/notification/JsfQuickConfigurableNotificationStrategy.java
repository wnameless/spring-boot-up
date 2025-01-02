package com.github.wnameless.spring.boot.up.notification;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.fsm.Phase;
import com.github.wnameless.spring.boot.up.fsm.PhaseProvider;
import com.github.wnameless.spring.boot.up.fsm.State;
import com.github.wnameless.spring.boot.up.fsm.Trigger;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaForm;
import com.github.wnameless.spring.boot.up.permission.role.Role;
import com.github.wnameless.spring.boot.up.web.RestfulItem;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;

public interface JsfQuickConfigurableNotificationStrategy< //
    CN extends NotificationConfigurationProvider, //
    NC extends NotificationCallback<NS, ID>, //
    NT extends NotificationTarget<NS, NR, M, ID>, //
    NS extends NotificationSource<ID>, //
    NR extends NotificationReceiver<M>, //
    M, //
    SM extends NotifiableStateMachine<SM, S, T> & Phase<E, S, T, ID>, //
    E extends PhaseProvider<E, S, T, ID> & JsonSchemaForm & RestfulItem<ID>, //
    S extends Enum<S> & State<T, ID>, //
    T extends Enum<T> & Trigger, //
    ID> extends QuickConfigurableNotificationStrategy<CN, NC, NT, NS, NR, M, SM, S, T, ID> {

  default String getPhaseName() {
    return SpringBootUp.getMessage(getNotifiableStateMachineType().getSimpleName(), null,
        getNotifiableStateMachineType().getSimpleName());
  }

  Set<Role> getRoleSet();

  BiFunction<CN, DocumentContext, DocumentContext> jsonPathSchemaStrategy();

  BiFunction<CN, DocumentContext, DocumentContext> jsonPathUiSchemaStrategy();

  BiFunction<CN, DocumentContext, DocumentContext> jsonPathFormDataStrategy();

  Map<String, Function<SM, String>> getDefaultPropertiesStrategyMap();

  default String getFormattedMessage(String message, List<String> properties, SM stateMachine) {
    var objs = new ArrayList<Object>();
    properties.forEach(props -> {
      if (getDefaultPropertiesStrategyMap().keySet().contains(props)) {
        objs.add(getDefaultPropertiesStrategyMap().get(props).apply(stateMachine));
      } else {
        var docCtx =
            JsonPath.using(Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build())
                .parse(stateMachine.getEntity().getFormData());
        var obj = docCtx.read(props);
        objs.add(obj == null ? "[ No Value! ]" : obj);
      }
    });

    return MessageFormat.format(message, objs.toArray());
  }

}
