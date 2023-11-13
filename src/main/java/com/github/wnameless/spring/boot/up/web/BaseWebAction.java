package com.github.wnameless.spring.boot.up.web;

import java.util.function.BiConsumer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import com.github.wnameless.spring.boot.up.web.function.QuadConsumer;
import com.github.wnameless.spring.boot.up.web.function.TriConsumer;

public interface BaseWebAction<D, ID> {

  default void indexPreAction(ModelAndView mav, MultiValueMap<String, String> params) {}

  void indexAction(ModelAndView mav, MultiValueMap<String, String> params);

  default void indexPostAction(ModelAndView mav, MultiValueMap<String, String> params) {}

  default BiConsumer<ModelAndView, MultiValueMap<String, String>> indexProcedure() {
    return (mav, params) -> {
      indexPreAction(mav, params);
      indexAction(mav, params);
      indexPostAction(mav, params);
    };
  }

  default void showPreAction(ID id, ModelAndView mav, MultiValueMap<String, String> params) {}

  void showAction(ID id, ModelAndView mav, MultiValueMap<String, String> params);

  default void showPostAction(ID id, ModelAndView mav, MultiValueMap<String, String> params) {}

  default TriConsumer<ID, ModelAndView, MultiValueMap<String, String>> showProcedure() {
    return (id, mav, params) -> {
      showPreAction(id, mav, params);
      showAction(id, mav, params);
      showPostAction(id, mav, params);
    };
  }

  default void newPreAction(ModelAndView mav, MultiValueMap<String, String> params) {}

  void newAction(ModelAndView mav, MultiValueMap<String, String> params);

  default void newPostAction(ModelAndView mav, MultiValueMap<String, String> params) {}

  default BiConsumer<ModelAndView, MultiValueMap<String, String>> newProcedure() {
    return (mav, params) -> {
      newPreAction(mav, params);
      newAction(mav, params);
      newPostAction(mav, params);
    };
  }

  default void createPreAction(ModelAndView mav, MultiValueMap<String, String> params, D data) {}

  void createAction(ModelAndView mav, MultiValueMap<String, String> params, D data);

  default void createPostAction(ModelAndView mav, MultiValueMap<String, String> params, D data) {}

  default TriConsumer<ModelAndView, MultiValueMap<String, String>, D> createProcedure() {
    return (mav, params, data) -> {
      createPreAction(mav, params, data);
      createAction(mav, params, data);
      createPostAction(mav, params, data);
    };
  }

  default void editPreAction(ID id, ModelAndView mav, MultiValueMap<String, String> params) {}

  void editAction(ID id, ModelAndView mav, MultiValueMap<String, String> params);

  default void editPostAction(ID id, ModelAndView mav, MultiValueMap<String, String> params) {}

  default TriConsumer<ID, ModelAndView, MultiValueMap<String, String>> editProcedure() {
    return (id, mav, params) -> {
      editPreAction(id, mav, params);
      editAction(id, mav, params);
      editPostAction(id, mav, params);
    };
  }

  default void updatePreAction(ID id, ModelAndView mav, MultiValueMap<String, String> params,
      D data) {}

  void updateAction(ID id, ModelAndView mav, MultiValueMap<String, String> params, D data);

  default void updatePostAction(ID id, ModelAndView mav, MultiValueMap<String, String> params,
      D data) {}

  default QuadConsumer<ID, ModelAndView, MultiValueMap<String, String>, D> updateProcedure() {
    return (id, mav, params, data) -> {
      updatePreAction(id, mav, params, data);
      updateAction(id, mav, params, data);
      updatePostAction(id, mav, params, data);
    };
  }

  default void deletePreAction(ID id, ModelAndView mav, MultiValueMap<String, String> params) {}

  void deleteAction(ID id, ModelAndView mav, MultiValueMap<String, String> params);

  default void deletePostAction(ID id, ModelAndView mav, MultiValueMap<String, String> params) {}

  default TriConsumer<ID, ModelAndView, MultiValueMap<String, String>> deleteProcedure() {
    return (id, mav, params) -> {
      deletePreAction(id, mav, params);
      deleteAction(id, mav, params);
      deletePostAction(id, mav, params);
    };
  }

}
