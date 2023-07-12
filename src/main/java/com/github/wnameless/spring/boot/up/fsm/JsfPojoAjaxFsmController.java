package com.github.wnameless.spring.boot.up.fsm;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
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
import com.github.wnameless.spring.boot.up.jsf.JsfPOJO;
import com.github.wnameless.spring.boot.up.jsf.RestfulJsonSchemaForm;
import com.github.wnameless.spring.boot.up.permission.resource.AccessControlRule;
import com.github.wnameless.spring.boot.up.web.BaseWebAction;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;
import com.github.wnameless.spring.boot.up.web.WebModelAttribute;

public interface JsfPojoAjaxFsmController<PA extends PhaseAware<PA, S, T, ID>, S extends State<T, ID>, T extends Trigger, D, ID>
    extends RestfulRouteProvider<ID>, BaseWebAction<D> {

  PA getPhaseAware();

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

  default BiFunction<JsfPOJO<?, ID>, PA, JsfPOJO<?, ID>> afterLoadJsf() {
    return null;
  }

  default BiFunction<JsfPOJO<?, ID>, PA, JsfPOJO<?, ID>> beforeSaveJsf() {
    return null;
  }

  default BiFunction<JsfPOJO<?, ID>, PA, JsfPOJO<?, ID>> afterSaveJsf() {
    return null;
  }

  CrudRepository<PA, ID> getPhaseRepository();

  @SuppressWarnings({"unchecked", "rawtypes"})
  @GetMapping(path = "/{id}/triggers/{triggerName}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView triggerAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String triggerName) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));

    PA phaseAware = getPhaseRepository().findById(id).get();
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
      getPhaseRepository().save(phaseAware);
      mav.addObject(WebModelAttribute.ITEM, phaseAware);
    }

    return mav;
  }

  @GetMapping(path = "/{id}/forms/{formType}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView showFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType, @RequestParam(required = true) String ajaxTargetId,
      @RequestParam(required = false) String embeddedTargetId,
      @RequestParam(required = false) String backTargetId) throws Exception {
    if (embeddedTargetId == null || embeddedTargetId.isBlank()) embeddedTargetId = ajaxTargetId;
    if (backTargetId == null || backTargetId.isBlank()) backTargetId = ajaxTargetId;
    mav.setViewName("jsf/form :: show-edit");
    mav.addObject("ajaxTargetId", ajaxTargetId);
    mav.addObject("backTargetId", backTargetId);
    mav.addObject("embeddedTargetId", embeddedTargetId);

    showAndEditAction(mav, id, formType, true);
    return mav;
  }


  @GetMapping(path = "/{id}/forms/{formType}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView editFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType, @RequestParam(required = true) String ajaxTargetId,
      @RequestParam(required = false) String backTargetId) throws Exception {
    if (backTargetId == null || backTargetId.isBlank()) backTargetId = ajaxTargetId;
    mav.setViewName("jsf/form :: edit");
    mav.addObject("ajaxTargetId", ajaxTargetId);
    mav.addObject("backTargetId", backTargetId);

    showAndEditAction(mav, id, formType, false);
    return mav;
  }

  @SuppressWarnings("unchecked")
  default void showAndEditAction(ModelAndView mav, ID id, String formType, boolean editable)
      throws Exception {
    PA phase = getPhaseRepository().findById(id).get();
    StateRecord<S, T, ID> stateRecord = phase.getStateRecord();
    S state = stateRecord.getState();

    Optional<StateForm<T, ID>> sfOpt = state.getForms().stream()
        .filter(item -> Objects.equals(item.formTypeStock().get(), formType)).findFirst();
    if (stateRecord.hasForm() && sfOpt.isPresent()) {
      StateForm<T, ID> sf = sfOpt.get();

      Map<String, Map<String, ID>> formDataTable = stateRecord.getFormDataTable();
      ID dataId = formDataTable.getOrDefault(formType, Collections.emptyMap())
          .get(sf.formBranchStock().get());

      JsfPOJO<?, ID> data;
      if (dataId == null) {
        data = (JsfPOJO<?, ID>) sf.jsfPojoType().getDeclaredConstructor().newInstance();
      } else {
        CrudRepository<JsfPOJO<?, ID>, ID> repo = SpringBootUp
            .findGenericBean(CrudRepository.class, sf.jsfPojoType(), id.getClass()).get();
        data = repo.findById(dataId).get();
      }
      if (afterLoadJsf() != null) {
        data = afterLoadJsf().apply(data, phase);
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
      mav.addObject(WebModelAttribute.ITEM, item);
    }
  }

  @SuppressWarnings("unchecked")
  @RequestMapping(path = "/{id}/forms/{formType}",
      method = {RequestMethod.PUT, RequestMethod.PATCH},
      consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView updateFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType, @RequestBody Map<String, Object> formData,
      @RequestParam(required = true) String ajaxTargetId,
      @RequestParam(required = true) String backTargetId) throws Exception {
    mav.setViewName("jsf/form :: show-edit");
    mav.addObject("ajaxTargetId", backTargetId);
    mav.addObject("embeddedTargetId", ajaxTargetId);

    PA phase = getPhaseRepository().findById(id).get();
    StateRecord<S, T, ID> stateRecord = phase.getStateRecord();
    S state = stateRecord.getState();

    Optional<StateForm<T, ID>> sfOpt = state.getForms().stream()
        .filter(item -> Objects.equals(item.formTypeStock().get(), formType)).findFirst();
    if (stateRecord.hasForm() && sfOpt.isPresent()) {
      StateForm<T, ID> sf = sfOpt.get();

      Map<String, Map<String, ID>> formDataTable = stateRecord.getFormDataTable();
      ID dataId = formDataTable.getOrDefault(formType, Collections.emptyMap())
          .get(sf.formBranchStock().get());

      JsfPOJO<?, ID> data;
      CrudRepository<JsfPOJO<?, ID>, ID> repo =
          SpringBootUp.findGenericBean(CrudRepository.class, sf.jsfPojoType(), id.getClass()).get();
      boolean notFound = dataId == null;
      if (notFound) {
        data = (JsfPOJO<?, ID>) sf.jsfPojoType().getDeclaredConstructor().newInstance();
      } else {
        data = repo.findById(dataId).get();
      }

      data.setFormData(formData);
      if (beforeSaveJsf() != null) {
        data = beforeSaveJsf().apply(data, phase);
      }
      repo.save(data);
      formDataTable.computeIfAbsent(formType, k -> new LinkedHashMap<>())
          .put(sf.formBranchStock().get(), data.getId());
      getPhaseRepository().save(phase);
      if (afterSaveJsf() != null) {
        data = afterSaveJsf().apply(data, phase);
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
      mav.addObject(WebModelAttribute.ITEM, item);
    }

    return mav;
  }

}
