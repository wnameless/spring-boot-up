package com.github.wnameless.spring.boot.up.fsm;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.MediaType;
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
import com.github.wnameless.spring.boot.up.jsf.JsfVersioning;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaForm;
import com.github.wnameless.spring.boot.up.jsf.RestfulJsonSchemaForm;
import com.github.wnameless.spring.boot.up.jsf.repository.JsfDataRepository;
import com.github.wnameless.spring.boot.up.jsf.service.JsfService;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControlRule;
import com.github.wnameless.spring.boot.up.web.BaseWebAction;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.AjaxTargetId;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.EmbeddedTargetId;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Item;
import com.github.wnameless.spring.boot.up.web.RestfulItemProvider;
import com.github.wnameless.spring.boot.up.web.RestfulRepositoryProvider;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;
import lombok.SneakyThrows;
import net.sf.rubycollect4j.Ruby;

public interface AjaxFsmController<SF extends JsonSchemaForm & JsfVersioning, PA extends PhaseAware<PA, S, T, ID>, S extends State<T, ID>, T extends Trigger, D, ID>
    extends RestfulRepositoryProvider<PA, ID>, RestfulItemProvider<PA>, RestfulRouteProvider<ID>,
    BaseWebAction<D> {

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
    Field[] fields = entityClass.getDeclaredFields();

    for (Field field : fields) {
      if (field.isAnnotationPresent(Id.class)) {
        field.setAccessible(true);
        return (ID) field.get(stateForm);
      }
    }
    throw new RuntimeException("Field annotated with @Id not found!");
  }

  default PA getPhaseAware() {
    return getRestfulItem();
  }

  default void excuateAlwaysTriggers() {
    for (T alwaysTrigger : getPhaseAware().getPhase().getAlwaysTriggers()) {
      if (getPhaseAware().getPhase().getStateMachine().canFire(alwaysTrigger)) {
        getPhaseAware().getPhase().getStateMachine().fire(alwaysTrigger);
      }
    }
  }

  @Override
  default void showPreAction(ModelAndView mav) {
    excuateAlwaysTriggers();
  }

  default BiFunction<PA, T, ?> getTriggerParameterStrategy() {
    return null;
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default BiFunction<PA, SF, SF> afterLoadStateForm() {
    return (phaseAware, stateForm) -> {
      List<StateFormAdvice> stateFormAdvices = SpringBootUp
          .findAllGenericBeans(StateFormAdvice.class, stateForm.getClass(), phaseAware.getClass())
          .stream().filter(advice -> advice.activeStatus().getAsBoolean()).toList();
      stateFormAdvices = Ruby.Array.of(stateFormAdvices).sortBy(sfa -> sfa.getOrder());
      for (var sfa : stateFormAdvices) {
        if (sfa.afterLoad() != null) {
          stateForm = (SF) sfa.afterLoad().apply(phaseAware, stateForm);
        }
      }
      return stateForm;
    };
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default BiFunction<PA, SF, SF> beforeSaveStateForm() {
    return (phaseAware, stateForm) -> {
      List<StateFormAdvice> stateFormAdvices = SpringBootUp
          .findAllGenericBeans(StateFormAdvice.class, stateForm.getClass(), phaseAware.getClass())
          .stream().filter(advice -> advice.activeStatus().getAsBoolean()).toList();
      stateFormAdvices = Ruby.Array.of(stateFormAdvices).sortBy(sfa -> sfa.getOrder());
      for (var sfa : stateFormAdvices) {
        if (sfa.beforeSave() != null) {
          stateForm = (SF) sfa.beforeSave().apply(phaseAware, stateForm);
        }
      }
      return stateForm;
    };
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  default BiFunction<PA, SF, SF> afterSaveStateForm() {
    return (phaseAware, stateForm) -> {
      List<StateFormAdvice> stateFormAdvices = SpringBootUp
          .findAllGenericBeans(StateFormAdvice.class, stateForm.getClass(), phaseAware.getClass())
          .stream().filter(advice -> advice.activeStatus().getAsBoolean()).toList();
      stateFormAdvices = Ruby.Array.of(stateFormAdvices).sortBy(sfa -> sfa.getOrder());
      for (var sfa : stateFormAdvices) {
        if (sfa.afterSave() != null) {
          stateForm = (SF) sfa.afterSave().apply(phaseAware, stateForm);
        }
      }
      return stateForm;
    };
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  @GetMapping(path = "/{id}/triggers/{triggerName}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView triggerAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String triggerName) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));

    PA phaseAware = getRestfulRepository().findById(id).get();
    StateMachine<S, T> stateMachine = phaseAware.getPhase().getStateMachine();

    T trigger = phaseAware.getPhase().getTrigger(triggerName);
    Object triggerParameter = null;
    if (getTriggerParameterStrategy() != null) {
      triggerParameter = getTriggerParameterStrategy().apply(phaseAware, trigger);
    }
    if (stateMachine.canFire(trigger)) {
      if (triggerParameter != null) {
        stateMachine.fire(new TriggerWithParameters1(trigger, triggerParameter.getClass()),
            triggerParameter);
      } else {
        stateMachine.fire(trigger);
      }
      StateRecord<S, T, ID> stateRecord = phaseAware.getStateRecord();
      stateRecord.setState(stateMachine.getState());
      phaseAware.setStateRecord(stateRecord);
      getRestfulRepository().save(phaseAware);
      mav.addObject(Item.name(), phaseAware);
    }

    return mav;
  }

  @GetMapping(path = "/{id}/forms/{formType}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView showFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType, @RequestParam(required = true) String ajaxTargetId,
      @RequestParam(required = false) String embeddedTargetId) {
    if (embeddedTargetId == null || embeddedTargetId.isBlank()) embeddedTargetId = ajaxTargetId;
    mav.setViewName("sbu/jsf/form :: show-edit");
    mav.addObject(AjaxTargetId.name(), ajaxTargetId);
    mav.addObject(EmbeddedTargetId.name(), embeddedTargetId);

    showAndEditAction(mav, id, formType, true);
    return mav;
  }

  @GetMapping(path = "/{id}/forms/{formType}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView editFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType, @RequestParam(required = true) String ajaxTargetId,
      @RequestParam(required = false) String backTargetId) {
    if (backTargetId == null || backTargetId.isBlank()) backTargetId = ajaxTargetId;
    mav.setViewName("sbu/jsf/form :: edit");
    mav.addObject(AjaxTargetId.name(), ajaxTargetId);
    mav.addObject(EmbeddedTargetId.name(), backTargetId);

    showAndEditAction(mav, id, formType, false);
    return mav;
  }

  default void showAndEditAction(ModelAndView mav, ID id, String formType, boolean editable) {
    PA phase = getRestfulRepository().findById(id).get();
    StateRecord<S, T, ID> stateRecord = phase.getStateRecord();
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
      if (afterLoadStateForm() != null) {
        data = afterLoadStateForm().apply(phase, data);
      }

      RestfulJsonSchemaForm<String> item = new RestfulJsonSchemaForm<>(
          getRestfulRoute().joinPath(getRestfulRoute().idToParam(id), "forms"), formType);
      item.setIndexPath(getRestfulRoute().getShowPath(id));
      item.setSchema(data.getSchema());
      item.setUiSchema(data.getUiSchema());
      item.setFormData(data.getFormData());
      item.setUpdatable(new AccessControlRule(true,
          () -> phase.getPhase().getStateMachine().canFire(sf.editableTriggerStock().get())));
      item.setBackPathname(getRestfulRoute().joinPath(getRestfulRoute().idToParam(id)));
      mav.addObject(Item.name(), item);
    }
  }

  @RequestMapping(path = "/{id}/forms/{formType}",
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView updateFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType, @RequestBody Map<String, Object> formData,
      @RequestParam(required = true) String ajaxTargetId,
      @RequestParam(required = true) String backTargetId) {
    mav.setViewName("sbu/jsf/form :: show-edit");
    mav.addObject(AjaxTargetId.name(), backTargetId);
    mav.addObject(EmbeddedTargetId.name(), ajaxTargetId);

    PA phase = getRestfulRepository().findById(id).get();
    StateRecord<S, T, ID> stateRecord = phase.getStateRecord();
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

      data.setFormData(formData);
      if (beforeSaveStateForm() != null) {
        data = beforeSaveStateForm().apply(phase, data);
      }
      data = saveStateForm(sf, data);
      stateRecord.putStateFormId(formType, sf.formBranchStock().get(), getStateFormId(data));
      getRestfulRepository().save(phase);
      if (afterSaveStateForm() != null) {
        data = afterSaveStateForm().apply(phase, data);
      }

      RestfulJsonSchemaForm<String> item = new RestfulJsonSchemaForm<>(
          getRestfulRoute().joinPath(getRestfulRoute().idToParam(id), "forms"), formType);
      item.setIndexPath(getRestfulRoute().getShowPath(id));
      item.setSchema(data.getSchema());
      item.setUiSchema(data.getUiSchema());
      item.setFormData(data.getFormData());
      item.setUpdatable(new AccessControlRule(true,
          () -> phase.getPhase().getStateMachine().canFire(sf.editableTriggerStock().get())));
      item.setBackPathname(getRestfulRoute().joinPath(getRestfulRoute().idToParam(id)));
      mav.addObject(Item.name(), item);
    }

    return mav;
  }

}
