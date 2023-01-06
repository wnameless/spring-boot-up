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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.github.oxo42.stateless4j.StateMachine;
import com.github.oxo42.stateless4j.triggers.TriggerWithParameters1;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.RestfulJsonSchemaForm;
import com.github.wnameless.spring.boot.up.jsf.model.JsfData;
import com.github.wnameless.spring.boot.up.jsf.model.JsfSchema;
import com.github.wnameless.spring.boot.up.jsf.repository.JsfDataRepository;
import com.github.wnameless.spring.boot.up.jsf.service.JsfService;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;

public interface AjaxFsmController<JD extends JsfData<JS, ID>, JS extends JsfSchema<ID>, P extends Phase<P, S, T, ID>, S extends State<T>, T extends Trigger, ID>
    extends RestfulRouteProvider<ID> {

  default BiFunction<P, T, ?> getTriggerParameterStrategy() {
    return null;
  }

  default BiFunction<JD, P, JD> afterLoadJsf() {
    return null;
  }

  default BiFunction<JD, P, JD> beforeSaveJsf() {
    return null;
  }

  default BiFunction<JD, P, JD> afterSaveJsf() {
    return null;
  }

  CrudRepository<P, ID> getPhaseRepository();

  @SuppressWarnings({"unchecked", "rawtypes"})
  @GetMapping(path = "/{id}/triggers/{triggerName}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView triggerAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String triggerName) {
    mav.setViewName(getRestfulRoute().toTemplateRoute().joinPath("show :: partial"));

    P phase = getPhaseRepository().findById(id).get();
    StateMachine<S, T> stateMachine = phase.getStateMachine();

    T trigger = phase.getTrigger(triggerName);
    Object triggerParameter = null;
    if (getTriggerParameterStrategy() != null) {
      triggerParameter = getTriggerParameterStrategy().apply(phase, trigger);
    }
    if (stateMachine.canFire(trigger)) {
      if (triggerParameter != null) {
        stateMachine.fire(new TriggerWithParameters1(trigger, triggerParameter.getClass()),
            triggerParameter);
      } else {
        stateMachine.fire(trigger);
      }
      StateRecord<S, T, ID> stateRecord = phase.getStateRecord();
      stateRecord.setState(stateMachine.getState());
      phase.setStateRecord(stateRecord);
      getPhaseRepository().save(phase);
      mav.addObject("item", phase);
    }

    return mav;
  }

  @GetMapping(path = "/{id}/forms/{formType}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView showFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType, @RequestParam(required = true) String ajaxTargetId,
      @RequestParam(required = false) String embeddedId) {
    if (embeddedId == null || embeddedId.isBlank()) embeddedId = ajaxTargetId;
    mav.setViewName(String.format("jsf/form :: show-edit(ajaxTargetId='%s', embeddedId='%s')",
        ajaxTargetId, embeddedId));
    showAndEditAction(mav, id, formType, true);
    return mav;
  }


  @GetMapping(path = "/{id}/forms/{formType}/edit", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView editFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType, @RequestParam(required = true) String ajaxTargetId,
      @RequestParam(required = false) String backTargetId) {
    if (backTargetId == null || backTargetId.isBlank()) backTargetId = ajaxTargetId;
    mav.setViewName(String.format("jsf/form :: edit(ajaxTargetId='%s', backTargetId='%s')",
        ajaxTargetId, backTargetId));
    showAndEditAction(mav, id, formType, false);
    return mav;
  }

  @SuppressWarnings("unchecked")
  default void showAndEditAction(ModelAndView mav, ID id, String formType, boolean editable) {
    P phase = getPhaseRepository().findById(id).get();
    StateRecord<S, T, ID> stateRecord = phase.getStateRecord();
    S state = stateRecord.getState();

    Optional<StateForm<T>> sfOpt = state.getForms().stream()
        .filter(item -> Objects.equals(item.formTypeStock().get(), formType)).findFirst();
    if (stateRecord.hasForm() && sfOpt.isPresent()) {
      StateForm<T> sf = sfOpt.get();

      Map<String, Map<String, ID>> formDataTable = stateRecord.getFormDataTable();
      ID dataId = formDataTable.getOrDefault(formType, Collections.emptyMap())
          .get(sf.formBranchStock().get());

      JD data;
      if (dataId == null) {
        data = (JD) SpringBootUp.getBean(JsfService.class).newJsfData(formType,
            sf.formBranchStock().get());
      } else {
        data = (JD) SpringBootUp.getBean(JsfDataRepository.class).findById(dataId).get();
      }
      if (afterLoadJsf() != null) {
        data = afterLoadJsf().apply(data, phase);
      }

      RestfulJsonSchemaForm<String> item = new RestfulJsonSchemaForm<>(
          getRestfulRoute().joinPath(getRestfulRoute().idToParam(id), "forms"), formType);
      item.setIndexPath(getRestfulRoute().getShowPath(id));
      item.setSchema(data.getJsfSchema().getSchema());
      item.setUiSchema(data.getJsfSchema().getUiSchema());
      item.setFormData(data.getFormData());
      if (editable) {
        item.setEditable(phase.getStateMachine().canFire(sf.editableTriggerStock().get()));
      }
      item.setBackPathname(getRestfulRoute().joinPath(getRestfulRoute().idToParam(id)));
      mav.addObject("item", item);
    }
  }

  @SuppressWarnings("unchecked")
  @PostMapping(path = "/{id}/forms/{formType}", consumes = MediaType.APPLICATION_JSON_VALUE)
  default ModelAndView updateFormAjax(ModelAndView mav, @PathVariable ID id,
      @PathVariable String formType, @RequestBody Map<String, Object> formData,
      @RequestParam(required = true) String ajaxTargetId,
      @RequestParam(required = true) String backTargetId) {
    mav.setViewName(String.format("jsf/form :: show-edit(ajaxTargetId='%s', embeddedId='%s'",
        backTargetId, ajaxTargetId));

    P phase = getPhaseRepository().findById(id).get();
    StateRecord<S, T, ID> stateRecord = phase.getStateRecord();
    S state = stateRecord.getState();

    Optional<StateForm<T>> sfOpt = state.getForms().stream()
        .filter(item -> Objects.equals(item.formTypeStock().get(), formType)).findFirst();
    if (stateRecord.hasForm() && sfOpt.isPresent()) {
      StateForm<T> sf = sfOpt.get();

      Map<String, Map<String, ID>> formDataTable = stateRecord.getFormDataTable();
      ID dataId = formDataTable.getOrDefault(formType, Collections.emptyMap())
          .get(sf.formBranchStock().get());

      JD data;
      if (dataId == null) {
        data = (JD) SpringBootUp.getBean(JsfService.class).newJsfData(formType,
            sf.formBranchStock().get());
      } else {
        data = (JD) SpringBootUp.getBean(JsfDataRepository.class).findById(dataId).get();
      }

      data.setFormData(formData);
      if (beforeSaveJsf() != null) {
        data = beforeSaveJsf().apply(data, phase);
      }
      SpringBootUp.getBean(JsfDataRepository.class).save(data);
      formDataTable.computeIfAbsent(formType, k -> new LinkedHashMap<>())
          .put(sf.formBranchStock().get(), data.getId());
      getPhaseRepository().save(phase);
      if (afterSaveJsf() != null) {
        data = afterSaveJsf().apply(data, phase);
      }

      RestfulJsonSchemaForm<String> item = new RestfulJsonSchemaForm<>(
          getRestfulRoute().joinPath(getRestfulRoute().idToParam(id), "forms"), formType);
      item.setIndexPath(getRestfulRoute().getShowPath(id));
      item.setSchema(data.getJsfSchema().getSchema());
      item.setUiSchema(data.getJsfSchema().getUiSchema());
      item.setFormData(data.getFormData());
      item.setEditable(phase.getStateMachine().canFire(sf.editableTriggerStock().get()));
      item.setBackPathname(getRestfulRoute().joinPath(getRestfulRoute().idToParam(id)));
      mav.addObject("item", item);
    }

    return mav;
  }

}
