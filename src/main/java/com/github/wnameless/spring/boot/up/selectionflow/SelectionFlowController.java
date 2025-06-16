package com.github.wnameless.spring.boot.up.selectionflow;

import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.github.wnameless.spring.boot.up.web.TemplateFragmentAware;

public interface SelectionFlowController<ID> extends TemplateFragmentAware {

  SelectionFlow<ID> getSelectionFlow();

  @GetMapping(path = "selection-flow", consumes = MediaType.APPLICATION_JSON_VALUE)
  default public ModelAndView creationFlow(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    mav.addObject("selectionFlow", getSelectionFlow());
    if (getSelectionFlow().isEndpointReached(params)
        && getSelectionFlow().endpointModelAndViewStrategy() != null) {
      return getSelectionFlow().endpointModelAndViewStrategy().apply(mav);
    } else {
      mav.setViewName("sbu/selection-flows/navigator :: " + getFragmentName());
      return mav;
    }
  }

}
