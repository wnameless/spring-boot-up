package com.github.wnameless.spring.boot.up.web;

import org.springframework.ui.Model;

public interface BaseWebAction<D> {

    void indexAction(Model model);

    void showAction(Model model);

    void newAction(Model model);

    void createAction(Model model, D data);

    void editAction(Model model);

    void updateAction(Model model, D data);

    void deleteAction(Model model);

}
