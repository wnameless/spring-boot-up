package com.github.wnameless.spring.boot.up.fsm;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.fsm.autoexecutor.AutoExecutorUtils;
import com.github.wnameless.spring.boot.up.jsf.JsfVersioning;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaForm;
import com.github.wnameless.spring.boot.up.jsf.RestfulVersioningJsonSchemaForm;
import com.github.wnameless.spring.boot.up.jsf.repository.JsfDataRepository;
import com.github.wnameless.spring.boot.up.jsf.service.JsfService;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControlRule;
import com.github.wnameless.spring.boot.up.web.BaseWebAction;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Alert;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Item;
import com.github.wnameless.spring.boot.up.web.RestfulItemProvider;
import com.github.wnameless.spring.boot.up.web.RestfulRepositoryProvider;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;
import com.github.wnameless.spring.boot.up.web.TemplateFragmentAware;
import com.github.wnameless.spring.boot.up.web.WebActionAlertHelper.AlertMessages;
import com.google.common.base.MoreObjects;
import io.github.wimdeblauwe.htmx.spring.boot.mvc.HxTrigger;
import jakarta.validation.Validator;
import lombok.SneakyThrows;
import net.sf.rubycollect4j.Ruby;

public interface AjaxFsmController<SF extends JsonSchemaForm & JsfVersioning, PP extends PhaseProvider<PP, S, T, ID>, S extends State<T, ID>, T extends Trigger, D, ID>
    extends RestfulRepositoryProvider<PP, ID>, RestfulItemProvider<PP>, RestfulRouteProvider<ID>,
    BaseWebAction<D, ID>, TemplateFragmentAware {

  org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(AjaxFsmController.class);

  @SneakyThrows
  @SuppressWarnings("unchecked")
  default SF newStateForm(StateForm<T, ID> sf, String formType) {
    if (sf.isJsfPojo()) {
      return (SF) sf.jsfPojoType().getDeclaredConstructor().newInstance();
    } else {
      return (SF) SpringBootUp.getBean(JsfService.class).newJsfData(formType,
          sf.formBranchStock().get());
    }
  }

  @SuppressWarnings("unchecked")
  default SF getStateForm(StateForm<T, ID> sf, ID formId) {
    if (sf.isJsfPojo()) {
      CrudRepository<SF, ID> repo =
          (CrudRepository<SF, ID>) SpringBootUp.getBean(sf.jsfRepositoryType());
      return repo.findById(formId).get();
    } else {
      return (SF) SpringBootUp.getBean(JsfDataRepository.class).findById(formId).get();
    }
  }

  @SuppressWarnings("unchecked")
  default SF saveStateForm(StateForm<T, ID> sf, SF stateForm) {
    if (sf.isJsfPojo()) {
      CrudRepository<SF, ID> repo =
          (CrudRepository<SF, ID>) SpringBootUp.getBean(sf.jsfRepositoryType());
      return repo.save(stateForm);
    } else {
      return (SF) SpringBootUp.getBean(JsfDataRepository.class).save(stateForm);
    }
  }

  @SneakyThrows
  @SuppressWarnings("unchecked")
  default ID getStateFormId(SF stateForm) {
    Class<?> entityClass = stateForm.getClass();
    List<Field> fields = FieldUtils.getAllFieldsList(entityClass);

    for (Field field : fields) {
      if (field.isAnnotationPresent(Id.class)
          || field.isAnnotationPresent(jakarta.persistence.Id.class)) {
        field.setAccessible(true);
        return (ID) field.get(stateForm);
      }
    }

    throw new RuntimeException("Field annotated with @Id not found!");
  }

  default PP getPhaseAware() {
    return getRestfulItem();
  }

  @SuppressWarnings("unchecked")
  default void excuateAlwaysTriggers() {
    var phase = getPhaseAware().getPhase();
    var stateRecord = getPhaseAware().getStateRecord();
    for (var alwaysTrigger : (List<T>) AutoExecutorUtils.getAlwaysTriggers(phase)) {
      var stateMachine =
          new StateMachine<>(stateRecord != null ? stateRecord.getState() : phase.initialState(),
              phase.getStateMachineConfigInternal());
      if (stateMachine.canFire(alwaysTrigger)) {
        stateMachine.fire(alwaysTrigger);
      }
    }

    // for (T alwaysTrigger : getPhaseAware().getPhase().getAlwaysTriggers()) {
    // if (getPhaseAware().getPhase().getStateMachine().canFire(alwaysTrigger)) {
    // getPhaseAware().getPhase().getStateMachine().fire(alwaysTrigger);
    // }
    // }
  }

  @Override
  default void showPreAction(ID id, ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    excuateAlwaysTriggers();

    var alertMessages = getAlertMessagesByStateConditions(getPhaseAware(), id);
    if (alertMessages.isPresent()) mav.addObject(Alert.name(), alertMessages);
  }

  default BiFunction<PP, T, ?> getTriggerParameterStrategy() {
    return null;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default TriFunction<PP, SF, RestfulVersioningJsonSchemaForm<?>, RestfulVersioningJsonSchemaForm<?>> afterLoadStateForm() {
    return (phaseAware, stateForm, jsf) -> {
      List<StateFormAdvice> stateFormAdvices = SpringBootUp
          .findAllGenericBeans(StateFormAdvice.class, stateForm.getClass(), phaseAware.getClass())
          .stream().filter(advice -> advice.activeStatus().getAsBoolean()).toList();
      stateFormAdvices = Ruby.Array.of(stateFormAdvices).sortBy(sfa -> sfa.getOrder());
      for (var sfa : stateFormAdvices) {
        if (sfa.afterLoad() != null) {
          jsf = (RestfulVersioningJsonSchemaForm<?>) sfa.afterLoad().apply(phaseAware, jsf);
        }
      }
      return jsf;
    };
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default TriFunction<PP, SF, RestfulVersioningJsonSchemaForm<?>, RestfulVersioningJsonSchemaForm<?>> beforeSaveStateForm() {
    return (phaseAware, stateForm, jsf) -> {
      List<StateFormAdvice> stateFormAdvices = SpringBootUp
          .findAllGenericBeans(StateFormAdvice.class, stateForm.getClass(), phaseAware.getClass())
          .stream().filter(advice -> advice.activeStatus().getAsBoolean()).toList();
      stateFormAdvices = Ruby.Array.of(stateFormAdvices).sortBy(sfa -> sfa.getOrder());
      for (var sfa : stateFormAdvices) {
        if (sfa.beforeSave() != null) {
          jsf = (RestfulVersioningJsonSchemaForm<?>) sfa.beforeSave().apply(phaseAware, jsf);
        }
      }
      return jsf;
    };
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default TriFunction<PP, SF, RestfulVersioningJsonSchemaForm<?>, RestfulVersioningJsonSchemaForm<?>> afterSaveStateForm() {
    return (phaseAware, stateForm, jsf) -> {
      List<StateFormAdvice> stateFormAdvices = SpringBootUp
          .findAllGenericBeans(StateFormAdvice.class, stateForm.getClass(), phaseAware.getClass())
          .stream().filter(advice -> advice.activeStatus().getAsBoolean()).toList();
      stateFormAdvices = Ruby.Array.of(stateFormAdvices).sortBy(sfa -> sfa.getOrder());
      for (var sfa : stateFormAdvices) {
        if (sfa.afterSave() != null) {
          jsf = (RestfulVersioningJsonSchemaForm<?>) sfa.afterSave().apply(phaseAware, jsf);
        }
      }
      return jsf;
    };
  }

  @GetMapping(path = "/{id}/triggers", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView refreshTriggersAjax(ModelAndView mav, @PathVariable ID id) {
    mav.setViewName("sbu/fsm/action-bar :: " + getFragmentName() + "/div.card-body");

    PP phaseProvider = getRestfulRepository().findById(id).get();
    mav.addObject(Item.name(), phaseProvider);

    return mav;
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @GetMapping(path = "/{id}/triggers/{triggerName}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView triggerAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String triggerName) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));

    PP phaseProvider = getRestfulRepository().findById(id).get();
    StateMachine<S, T> stateMachine = phaseProvider.getPhase().getStateMachine();

    T trigger = phaseProvider.getPhase().getTrigger(triggerName);
    Object triggerParameter = null;
    if (getTriggerParameterStrategy() != null) {
      triggerParameter = getTriggerParameterStrategy().apply(phaseProvider, trigger);
    }
    if (stateMachine.canFire(trigger)) {
      if (triggerParameter != null) {
        stateMachine.fire(new TriggerWithParameters1(trigger, triggerParameter.getClass()),
            triggerParameter);
      } else {
        stateMachine.fire(trigger);
      }
      StateRecord<S, T, ID> stateRecord = phaseProvider.getPhase().getStateRecord();
      stateRecord.setState(stateMachine.getState());
      phaseProvider.setStateRecord(stateRecord);
      getRestfulRepository().save(phaseProvider);
      mav.addObject(Item.name(), phaseProvider);
    }

    var alertMessages = getAlertMessagesByStateConditions(phaseProvider, trigger, id);
    if (alertMessages.isPresent()) mav.addObject(Alert.name(), alertMessages);
    return mav;
  }

  @GetMapping(path = "/{id}/forms/{formType}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView showFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType) {
    mav.setViewName("sbu/jsf/show-edit-only :: " + getFragmentName());

    showAndEditAction(mav, id, formType);
    return mav;
  }

  @GetMapping(path = "/{id}/forms/{formType}/formId/{formId}",
      consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView showFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType, @PathVariable ID formId) {
    mav.setViewName("sbu/jsf/show-only :: " + getFragmentName());

    showAction(mav, id, formType, formId);
    return mav;
  }

  default void showAction(ModelAndView mav, ID id, String formType, ID formId) {
    PP phaseProvider = getRestfulRepository().findById(id).get();
    StateRecord<S, T, ID> stateRecord = phaseProvider.getPhase().getStateRecord();
    S state = stateRecord.getState();

    Optional<StateForm<T, ID>> sfOpt = state.getForms().stream()
        .filter(item -> Objects.equals(item.formTypeStock().get(), formType)).findFirst();
    if (stateRecord.hasForm() && sfOpt.isPresent()) {
      StateForm<T, ID> sf = sfOpt.get();

      SF data = this.getStateForm(sf, formId);

      RestfulVersioningJsonSchemaForm<?> item =
          new RestfulVersioningJsonSchemaForm<>(formType, sf.formBranchStock().get(),
              getRestfulRoute().joinPath(getRestfulRoute().idToParam(id), "forms"), formType);
      item.setIndexPath(getRestfulRoute().getShowPath(id));
      item.setSchema(data.getSchema());
      item.setUiSchema(data.getUiSchema());
      item.setFormData(data.getFormData());
      item.setUpdatable(new AccessControlRule(true, () -> phaseProvider.getPhase().getStateMachine()
          .canFire(sf.editableTriggerStock().get())));
      item.setBackPathname(getRestfulRoute().joinPath(getRestfulRoute().idToParam(id)));

      if (afterLoadStateForm() != null) {
        item = afterLoadStateForm().apply(phaseProvider, data, item);
      }

      mav.addObject(Item.name(), item);
    }
  }

  @GetMapping(path = "/{id}/forms/{formType}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView editFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType) {
    mav.setViewName("sbu/jsf/edit :: " + getFragmentName());

    showAndEditAction(mav, id, formType);
    return mav;
  }

  default void showAndEditAction(ModelAndView mav, ID id, String formType) {
    PP phaseProvider = getRestfulRepository().findById(id).get();
    StateRecord<S, T, ID> stateRecord = phaseProvider.getPhase().getStateRecord();
    S state = stateRecord.getState();

    Optional<StateForm<T, ID>> sfOpt = state.getForms().stream()
        .filter(item -> Objects.equals(item.formTypeStock().get(), formType)).findFirst();
    if (stateRecord.hasForm() && sfOpt.isPresent()) {
      StateForm<T, ID> sf = sfOpt.get();
      var dataIdOpt = stateRecord.findStateFormId(formType, sf.formBranchStock().get());

      SF data;
      if (dataIdOpt.isEmpty()) {
        data = newStateForm(sf, formType);
      } else {
        data = this.getStateForm(sf, dataIdOpt.get());
      }

      RestfulVersioningJsonSchemaForm<?> item =
          new RestfulVersioningJsonSchemaForm<>(formType, sf.formBranchStock().get(),
              getRestfulRoute().joinPath(getRestfulRoute().idToParam(id), "forms"), formType);
      item.setIndexPath(getRestfulRoute().getShowPath(id));
      item.setSchema(data.getSchema());
      item.setUiSchema(data.getUiSchema());
      item.setFormData(data.getFormData());
      item.setUpdatable(new AccessControlRule(true, () -> phaseProvider.getPhase().getStateMachine()
          .canFire(sf.editableTriggerStock().get())));
      item.setBackPathname(getRestfulRoute().joinPath(getRestfulRoute().idToParam(id)));

      if (afterLoadStateForm() != null) {
        item = afterLoadStateForm().apply(phaseProvider, data, item);
      }

      mav.addObject(Item.name(), item);
    }
  }

  @HxTrigger("refresh-fsm-action-bar")
  @RequestMapping(path = "/{id}/forms/{formType}",
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView updateFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType, @RequestBody Map<String, Object> formData) {
    mav.setViewName("sbu/jsf/show-edit-only-with-alert :: " + getFragmentName());

    PP phaseProvider = getRestfulRepository().findById(id).get();
    StateRecord<S, T, ID> stateRecord = phaseProvider.getPhase().getStateRecord();
    S state = stateRecord.getState();

    Optional<StateForm<T, ID>> sfOpt = state.getForms().stream()
        .filter(item -> Objects.equals(item.formTypeStock().get(), formType)).findFirst();
    if (stateRecord.hasForm() && sfOpt.isPresent()) {
      StateForm<T, ID> sf = sfOpt.get();
      var dataIdOpt = stateRecord.findStateFormId(formType, sf.formBranchStock().get());

      SF data;
      if (dataIdOpt.isEmpty()) {
        data = newStateForm(sf, formType);
      } else {
        data = getStateForm(sf, dataIdOpt.get());
      }

      RestfulVersioningJsonSchemaForm<?> item =
          new RestfulVersioningJsonSchemaForm<>(formType, sf.formBranchStock().get(),
              getRestfulRoute().joinPath(getRestfulRoute().idToParam(id), "forms"), formType);
      item.setIndexPath(getRestfulRoute().getShowPath(id));
      item.setSchema(data.getSchema());
      item.setUiSchema(data.getUiSchema());
      item.setFormData(data.getFormData());
      item.setUpdatable(new AccessControlRule(true, () -> phaseProvider.getPhase().getStateMachine()
          .canFire(sf.editableTriggerStock().get())));
      item.setBackPathname(getRestfulRoute().joinPath(getRestfulRoute().idToParam(id)));

      item.setFormData(formData);
      if (beforeSaveStateForm() != null) {
        item = beforeSaveStateForm().apply(phaseProvider, data, item);
      }

      data.setFormData(formData);
      ValidStateForm validStateForm =
          AnnotationUtils.findAnnotation(data.getClass(), ValidStateForm.class);
      List<String> messages = new ArrayList<>();
      if (validStateForm != null) {
        var validator = SpringBootUp.getBean(Validator.class);
        // validates bean
        messages.addAll(validator.validate(data).stream().map(e -> e.getMessage()).toList());
        if (messages.size() > 0) {
          var alertMessages = new AlertMessages();
          alertMessages.setWarning(messages);
          alertMessages.getDanger().add("Data NOT saved.");
          mav.addObject(Alert.name(), alertMessages);
        }
      }

      if (messages.isEmpty()) {
        data = saveStateForm(sf, data);
        stateRecord.putStateFormId(formType, sf.formBranchStock().get(), getStateFormId(data));
        getRestfulRepository().save(phaseProvider);

        if (afterSaveStateForm() != null) {
          item = afterSaveStateForm().apply(phaseProvider, data, item);
        }
      }

      mav.addObject(Item.name(), item);
    }

    return mav;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default AlertMessages getAlertMessagesByStateConditions(PP phaseProvider, T trigger, ID id) {
    AlertMessages alertMessages = new AlertMessages();
    StateMachine<S, T> stateMachine = phaseProvider.getPhase().getStateMachine();

    List<StateMessageCondition> stateCondition =
        SpringBootUp.findAllGenericBeans(StateMessageCondition.class, phaseProvider.getClass(),
            stateMachine.getState().getClass(), trigger.getClass(), id.getClass());
    for (var sc : stateCondition) {
      if (sc.onEntry(phaseProvider, stateMachine.getState()) instanceof Boolean b && b) {
        List.of("danger", "info", "success", "warning").forEach(t -> {
          String onEntryMsg = "sbu.fsm.message." + t + ".onEntry."
              + phaseProvider.getStateRecord().getState().getClass().getSimpleName() + "."
              + stateMachine.getState();
          try {
            SpringBootUp.getMessage(onEntryMsg); // Check if message exists
            var processedMsg =
                sc.processMessage(onEntryMsg, phaseProvider, stateMachine.getState());
            if (processedMsg != null) alertMessages.setUtext(true);
            switch (t) {
              case "danger" -> alertMessages.getDanger()
                  .add(MoreObjects.firstNonNull(processedMsg, onEntryMsg));
              case "info" -> alertMessages.getInfo()
                  .add(MoreObjects.firstNonNull(processedMsg, onEntryMsg));
              case "success" -> alertMessages.getSuccess()
                  .add(MoreObjects.firstNonNull(processedMsg, onEntryMsg));
              case "warning" -> alertMessages.getWarning()
                  .add(MoreObjects.firstNonNull(processedMsg, onEntryMsg));
            }
          } catch (Exception e) {}
        });
      }

      if (sc.onEntryFrom(phaseProvider, stateMachine.getState(), trigger) instanceof Boolean b
          && b) {
        List.of("danger", "info", "success", "warning").forEach(t -> {
          String onEntryFromMsg = "sbu.fsm.message." + t + ".onEntryFrom."
              + phaseProvider.getStateRecord().getState().getClass().getSimpleName() + "."
              + stateMachine.getState() + "." + trigger.getName();
          try {
            SpringBootUp.getMessage(onEntryFromMsg); // Check if message exists
            var processedMsg =
                sc.processMessage(onEntryFromMsg, phaseProvider, stateMachine.getState());
            if (processedMsg != null) alertMessages.setUtext(true);
            switch (t) {
              case "danger" -> alertMessages.getDanger()
                  .add(MoreObjects.firstNonNull(processedMsg, onEntryFromMsg));
              case "info" -> alertMessages.getInfo()
                  .add(MoreObjects.firstNonNull(processedMsg, onEntryFromMsg));
              case "success" -> alertMessages.getSuccess()
                  .add(MoreObjects.firstNonNull(processedMsg, onEntryFromMsg));
              case "warning" -> alertMessages.getWarning()
                  .add(MoreObjects.firstNonNull(processedMsg, onEntryFromMsg));
            }
          } catch (Exception e) {}
        });
      }
    }

    return alertMessages;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default AlertMessages getAlertMessagesByStateConditions(PP phaseProvider, ID id) {
    AlertMessages alertMessages = new AlertMessages();
    StateMachine<S, T> stateMachine = phaseProvider.getPhase().getStateMachine();

    List<StateMessageCondition> stateCondition = SpringBootUp.findAllGenericBeans(
        StateMessageCondition.class, phaseProvider.getClass(), stateMachine.getState().getClass(),
        phaseProvider.getPhase().getAllTriggers().getFirst().getClass(), id.getClass());
    for (var sc : stateCondition) {
      if (sc.on(phaseProvider, stateMachine.getState()) instanceof Boolean b && b) {
        List.of("danger", "info", "success", "warning").forEach(t -> {
          String onMsg = "sbu.fsm.message." + t + ".on."
              + phaseProvider.getStateRecord().getState().getClass().getSimpleName() + "."
              + stateMachine.getState();
          try {
            SpringBootUp.getMessage(onMsg); // Check if message exists
            var processedMsg = sc.processMessage(onMsg, phaseProvider, stateMachine.getState());
            if (processedMsg != null) alertMessages.setUtext(true);
            switch (t) {
              case "danger" -> alertMessages.getDanger()
                  .add(MoreObjects.firstNonNull(processedMsg, onMsg));
              case "info" -> alertMessages.getInfo()
                  .add(MoreObjects.firstNonNull(processedMsg, onMsg));
              case "success" -> alertMessages.getSuccess()
                  .add(MoreObjects.firstNonNull(processedMsg, onMsg));
              case "warning" -> alertMessages.getWarning()
                  .add(MoreObjects.firstNonNull(processedMsg, onMsg));
            }
          } catch (Exception e) {}
        });
      }
    }

    return alertMessages;
  }

}
