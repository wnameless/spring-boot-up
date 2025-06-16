package com.github.wnameless.spring.boot.up.selectionflow;

import static lombok.AccessLevel.PRIVATE;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Function;
import org.springframework.util.MultiValueMap;
import org.springframework.web.servlet.ModelAndView;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class SimpleSelectionFlow<ID> implements SelectionFlow<ID> {

  final List<String> paramNames;

  final Function<ModelAndView, ModelAndView> endpointModelAndViewStrategy;
  final Function<MultiValueMap<String, String>, LinkedHashMap<String, List<SelectionFlowOption<ID>>>> selectionOptionStrategy;

  @Override
  public Function<ModelAndView, ModelAndView> endpointModelAndViewStrategy() {
    return endpointModelAndViewStrategy;
  }

  @Override
  public Function<MultiValueMap<String, String>, LinkedHashMap<String, List<SelectionFlowOption<ID>>>> selectionOptionStrategy() {
    return selectionOptionStrategy;
  }

}
