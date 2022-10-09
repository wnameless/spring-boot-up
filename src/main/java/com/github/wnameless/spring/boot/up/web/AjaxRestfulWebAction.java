package com.github.wnameless.spring.boot.up.web;

import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface AjaxRestfulWebAction<D, ID>
        extends BaseWebAction<D>, RestfulRouteProvider<ID> {

    @GetMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    default String indexAjax(Model model) {
        indexAction(model);
        return getRestfulRoute().getTemplateRoute()
                .joinPath("index :: partial");
    }

    @GetMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    default String showAjax(Model model) {
        indexAction(model);
        return getRestfulRoute().getTemplateRoute().joinPath("show :: partial");
    }

    @GetMapping(path = "/new", consumes = MediaType.APPLICATION_JSON_VALUE)
    default String newAjax(Model model) {
        newAction(model);
        return getRestfulRoute().getTemplateRoute().joinPath("new :: partial");
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    default String createAjax(Model model, @RequestBody D data) {
        createAction(model, data);
        return getRestfulRoute().getTemplateRoute()
                .joinPath("index :: partial");
    }

    @GetMapping(path = "/{id}/edit",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    default String editAjax(Model model) {
        editAction(model);
        return getRestfulRoute().getTemplateRoute().joinPath("edit :: partial");
    }

    @PostMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    default String updateAjax(Model model, @RequestBody D data) {
        updateAction(model, data);
        return getRestfulRoute().getTemplateRoute()
                .joinPath("index :: partial");
    }

    @DeleteMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    default String deleteAjax(Model model) {
        deleteAction(model);
        return getRestfulRoute().getTemplateRoute()
                .joinPath("index :: partial");
    }

}