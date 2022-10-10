package com.github.wnameless.spring.boot.up.web;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface HtmlRestfulWebAction<D, ID>
        extends BaseWebAction<D>, RestfulRouteProvider<ID> {

    @GetMapping
    default String indexHtml(Model model) {
        indexAction(model);
        return getRestfulRoute().toTemplateRoute()
                .joinPath("index :: complete");
    }

    @GetMapping("/{id}")
    default String showHtml(Model model) {
        indexAction(model);
        return getRestfulRoute().toTemplateRoute().joinPath("show :: complete");
    }

    @GetMapping("/new")
    default String newHtml(Model model) {
        newAction(model);
        return getRestfulRoute().toTemplateRoute().joinPath("new :: complete");
    }

    @PostMapping
    default String createHtml(Model model, @RequestBody D data) {
        createAction(model, data);
        return getRestfulRoute().toTemplateRoute()
                .joinPath("index :: complete");
    }

    @GetMapping("/{id}/edit")
    default String editHtml(Model model) {
        editAction(model);
        return getRestfulRoute().toTemplateRoute().joinPath("edit :: complete");
    }

    @PostMapping("/{id}")
    default String updateHtml(Model model, @RequestBody D data) {
        updateAction(model, data);
        return getRestfulRoute().toTemplateRoute()
                .joinPath("index :: complete");
    }

    @DeleteMapping("/{id}")
    default String deleteHtml(Model model) {
        deleteAction(model);
        return getRestfulRoute().toTemplateRoute()
                .joinPath("index :: complete");
    }

}
