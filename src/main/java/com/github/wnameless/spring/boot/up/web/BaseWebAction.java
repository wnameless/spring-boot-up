package com.github.wnameless.spring.boot.up.web;

import java.util.function.BiConsumer;
import org.apache.commons.lang3.function.TriConsumer;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;

public interface BaseWebAction<D> {

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

  default void showPreAction(ModelAndView mav, MultiValueMap<String, String> params) {}

  void showAction(ModelAndView mav, MultiValueMap<String, String> params);

  default void showPostAction(ModelAndView mav, MultiValueMap<String, String> params) {}

  default BiConsumer<ModelAndView, MultiValueMap<String, String>> showProcedure() {
    return (mav, params) -> {
      showPreAction(mav, params);
      showAction(mav, params);
      showPostAction(mav, params);
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

  default void editPreAction(ModelAndView mav, MultiValueMap<String, String> params) {}

  void editAction(ModelAndView mav, MultiValueMap<String, String> params);

  default void editPostAction(ModelAndView mav, MultiValueMap<String, String> params) {}

  default BiConsumer<ModelAndView, MultiValueMap<String, String>> editProcedure() {
    return (mav, params) -> {
      editPreAction(mav, params);
      editAction(mav, params);
      editPostAction(mav, params);
    };
  }

  default void updatePreAction(ModelAndView mav, MultiValueMap<String, String> params, D data) {}

  void updateAction(ModelAndView mav, MultiValueMap<String, String> params, D data);

  default void updatePostAction(ModelAndView mav, MultiValueMap<String, String> params, D data) {}

  default TriConsumer<ModelAndView, MultiValueMap<String, String>, D> updateProcedure() {
    return (mav, params, data) -> {
      updatePreAction(mav, params, data);
      updateAction(mav, params, data);
      updatePostAction(mav, params, data);
    };
  }

  default void deletePreAction(ModelAndView mav, MultiValueMap<String, String> params) {}

  void deleteAction(ModelAndView mav, MultiValueMap<String, String> params);

  default void deletePostAction(ModelAndView mav, MultiValueMap<String, String> params) {}

  default BiConsumer<ModelAndView, MultiValueMap<String, String> > deleteProcedure() {
    return (mav,params) -> {
      deletePreAction(mav,params);
      deleteAction(mav,params);
      deletePostAction(mav,params);
    };
  }

}
