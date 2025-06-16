package com.github.wnameless.spring.boot.up.selectionflow;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;

public interface SelectionFlow<ID> {

  List<String> getParamNames();

  default boolean isEndpointReached(MultiValueMap<String, String> params) {
    for (var name : getParamNames()) {
      if (params.getFirst(name) == null) return false;
      if (params.getFirst(name).isBlank()) return false;
    }
    return true;
  }

  Function<ModelAndView, ModelAndView> endpointModelAndViewStrategy();

  Function<MultiValueMap<String, String>, LinkedHashMap<String, List<SelectionFlowOption<ID>>>> selectionOptionStrategy();

  default LinkedHashMap<String, List<SelectionFlowOption<ID>>> getSelectionOptions(
      MultiValueMap<String, String> params) {
    return selectionOptionStrategy().apply(params);
  }

}
