package com.github.wnameless.spring.boot.up.web;

import java.util.Map;
import java.util.Objects;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import com.github.wnameless.spring.boot.up.web.Pageables.PageableParams;
import jakarta.servlet.http.HttpSession;

public final class SessionModel {

  public static SessionModel of(Model model, HttpSession session) {
    return new SessionModel(model, session);
  }

  private final Model model;
  private final HttpSession session;

  public SessionModel(Model model, HttpSession session) {
    Objects.requireNonNull(model);
    Objects.requireNonNull(session);

    this.model = model;
    this.session = session;
  }

  public <E> E initAttr(String key, E value) {
    return initAttr(key, value, null);
  }

  @SuppressWarnings("unchecked")
  public <E> E initAttr(String key, E value, E defaultVal) {
    if (value == null) {
      value = (E) session.getAttribute(key);
    }
    if (value == null) value = defaultVal;

    model.addAttribute(key, value);
    session.setAttribute(key, value);

    return value;
  }

  @SuppressWarnings("unchecked")
  public <E> E initAttr(String key, E value, boolean skipSessionLookUp) {
    if (!skipSessionLookUp) {
      value = (E) session.getAttribute(key);
    }

    model.addAttribute(key, value);
    session.setAttribute(key, value);

    return value;
  }

  public Pageable initPageable(Map<String, String> requestParams) {
    return initPageable(requestParams, PageableParams.ofSpring());
  }

  public Pageable initPageable(Map<String, String> requestParams, PageableParams pageableParams) {
    String pageParam = pageableParams.getPageParameter();
    String sizeParam = pageableParams.getSizeParameter();
    String sortParam = pageableParams.getSortParameter();
    String page = initAttr(pageParam, requestParams.get(pageParam), "0");
    String size = initAttr(sizeParam, requestParams.get(sizeParam), "10");
    String sort = initAttr(sortParam, requestParams.get(sortParam), "");

    return initAttr("pageable",
        PageRequest.of(Integer.valueOf(page), Integer.valueOf(size), Pageables.paramToSort(sort)));
  }

  public Pageable initPageable(Map<String, String> requestParams, Pageable defaultPageable) {
    String page = initAttr("page", requestParams.get("page"),
        Integer.toString(defaultPageable.getPageNumber()));
    String size = initAttr("size", requestParams.get("size"),
        Integer.toString(defaultPageable.getPageSize()));
    String sort = initAttr("sort", requestParams.get("sort"),
        Pageables.sortToParam(defaultPageable.getSort()).get(0));

    return initAttr("pageable",
        PageRequest.of(Integer.valueOf(page), Integer.valueOf(size), Pageables.paramToSort(sort)));
  }

}
