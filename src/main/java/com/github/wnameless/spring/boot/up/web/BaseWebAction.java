package com.github.wnameless.spring.boot.up.web;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.springframework.web.servlet.ModelAndView;

public interface BaseWebAction<D> {

  default void indexPreAction(ModelAndView mav) {}

  void indexAction(ModelAndView mav);

  default void indexPostAction(ModelAndView mav) {}

  default Consumer<ModelAndView> indexProcedure() {
    return mav -> {
      indexPreAction(mav);
      indexAction(mav);
      indexPostAction(mav);
    };
  }

  default void showPreAction(ModelAndView mav) {}

  void showAction(ModelAndView mav);

  default void showPostAction(ModelAndView mav) {}

  default Consumer<ModelAndView> showProcedure() {
    return mav -> {
      showPreAction(mav);
      showAction(mav);
      showPostAction(mav);
    };
  }

  default void newPreAction(ModelAndView mav) {}

  void newAction(ModelAndView mav);

  default void newPostAction(ModelAndView mav) {}

  default Consumer<ModelAndView> newProcedure() {
    return mav -> {
      newPreAction(mav);
      newAction(mav);
      newPostAction(mav);
    };
  }

  default void createPreAction(ModelAndView mav, D data) {}

  void createAction(ModelAndView mav, D data);

  default void createPostAction(ModelAndView mav, D data) {}

  default BiConsumer<ModelAndView, D> createProcedure() {
    return (mav, data) -> {
      createPreAction(mav, data);
      createAction(mav, data);
      createPostAction(mav, data);
    };
  }

  default void editPreAction(ModelAndView mav) {}

  void editAction(ModelAndView mav);

  default void editPostAction(ModelAndView mav) {}

  default Consumer<ModelAndView> editProcedure() {
    return mav -> {
      editPreAction(mav);
      editAction(mav);
      editPostAction(mav);
    };
  }

  default void updatePreAction(ModelAndView mav, D data) {}

  void updateAction(ModelAndView mav, D data);

  default void updatePostAction(ModelAndView mav, D data) {}

  default BiConsumer<ModelAndView, D> updateProcedure() {
    return (mav, data) -> {
      updatePreAction(mav, data);
      updateAction(mav, data);
      updatePostAction(mav, data);
    };
  }

  default void deletePreAction(ModelAndView mav) {}

  void deleteAction(ModelAndView mav);

  default void deletePostAction(ModelAndView mav) {}

  default Consumer<ModelAndView> deleteProcedure() {
    return mav -> {
      deletePreAction(mav);
      deleteAction(mav);
      deletePostAction(mav);
    };
  }

}
