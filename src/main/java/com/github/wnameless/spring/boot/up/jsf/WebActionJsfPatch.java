package com.github.wnameless.spring.boot.up.jsf;

import java.util.Map;
import java.util.function.Function;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.service.JsfPatchService;
import com.github.wnameless.spring.boot.up.web.BaseWebAction;

public interface WebActionJsfPatch<D, ID> extends BaseWebAction<D, ID> {

  default JsfPatchService getJsfPatchService() {
    return SpringBootUp.getBean(JsfPatchService.class);
  }

  @Override
  default void indexPostAction(ModelAndView mav, MultiValueMap<String, String> params) {
    if (indexActionSchemaPatch() != null) {
      getJsfPatchService().schemaPatch(indexActionSchemaPatch());
    }
    if (indexActionUiSchemaPatch() != null) {
      getJsfPatchService().uiSchemaPatch(indexActionUiSchemaPatch());
    }
    if (indexActionFormDataPatch() != null) {
      getJsfPatchService().formDataPatch(indexActionFormDataPatch());
    }
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> indexActionSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> indexActionUiSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> indexActionFormDataPatch() {
    return null;
  }

  @Override
  default void showPostAction(ID id, ModelAndView mav, MultiValueMap<String, String> params) {
    if (showActionSchemaPatch() != null) {
      getJsfPatchService().schemaPatch(showActionSchemaPatch());
    }
    if (showActionUiSchemaPatch() != null) {
      getJsfPatchService().uiSchemaPatch(showActionUiSchemaPatch());
    }
    if (showActionFormDataPatch() != null) {
      getJsfPatchService().formDataPatch(showActionFormDataPatch());
    }
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> showActionSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> showActionUiSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> showActionFormDataPatch() {
    return null;
  }

  @Override
  default void newPostAction(ModelAndView mav, MultiValueMap<String, String> params) {
    if (newActionSchemaPatch() != null) {
      getJsfPatchService().schemaPatch(newActionSchemaPatch());
    }
    if (newActionUiSchemaPatch() != null) {
      getJsfPatchService().uiSchemaPatch(newActionUiSchemaPatch());
    }
    if (newActionFormDataPatch() != null) {
      getJsfPatchService().formDataPatch(newActionFormDataPatch());
    }
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> newActionSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> newActionUiSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> newActionFormDataPatch() {
    return null;
  }

  @Override
  default void createPostAction(ModelAndView mav, MultiValueMap<String, String> params, D data) {
    if (createActionSchemaPatch() != null) {
      getJsfPatchService().schemaPatch(createActionSchemaPatch());
    }
    if (createActionUiSchemaPatch() != null) {
      getJsfPatchService().uiSchemaPatch(createActionUiSchemaPatch());
    }
    if (createActionFormDataPatch() != null) {
      getJsfPatchService().formDataPatch(createActionFormDataPatch());
    }
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> createActionSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> createActionUiSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> createActionFormDataPatch() {
    return null;
  }

  @Override
  default void editPostAction(ID id, ModelAndView mav, MultiValueMap<String, String> params) {
    if (editActionSchemaPatch() != null) {
      getJsfPatchService().schemaPatch(editActionSchemaPatch());
    }
    if (editActionUiSchemaPatch() != null) {
      getJsfPatchService().uiSchemaPatch(editActionUiSchemaPatch());
    }
    if (editActionFormDataPatch() != null) {
      getJsfPatchService().formDataPatch(editActionFormDataPatch());
    }
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> editActionSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> editActionUiSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> editActionFormDataPatch() {
    return null;
  }

  @Override
  default void updatePostAction(ID id, ModelAndView mav, MultiValueMap<String, String> params,
      D data) {
    if (updateActionSchemaPatch() != null) {
      getJsfPatchService().schemaPatch(updateActionSchemaPatch());
    }
    if (updateActionUiSchemaPatch() != null) {
      getJsfPatchService().uiSchemaPatch(updateActionUiSchemaPatch());
    }
    if (updateActionFormDataPatch() != null) {
      getJsfPatchService().formDataPatch(updateActionFormDataPatch());
    }
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> updateActionSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> updateActionUiSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> updateActionFormDataPatch() {
    return null;
  }

  @Override
  default void deletePostAction(ID id, ModelAndView mav, MultiValueMap<String, String> params) {
    if (deleteActionSchemaPatch() != null) {
      getJsfPatchService().schemaPatch(deleteActionSchemaPatch());
    }
    if (deleteActionUiSchemaPatch() != null) {
      getJsfPatchService().uiSchemaPatch(deleteActionUiSchemaPatch());
    }
    if (deleteActionFormDataPatch() != null) {
      getJsfPatchService().formDataPatch(deleteActionFormDataPatch());
    }
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> deleteActionSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> deleteActionUiSchemaPatch() {
    return null;
  }

  default Function<? super JsonSchemaForm, Map<String, Object>> deleteActionFormDataPatch() {
    return null;
  }

}
