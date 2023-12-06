package com.github.wnameless.spring.boot.up.tagging;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;

public interface TaggingController<LT extends LabelTag<TT, ID>, TT extends TagTemplate, ID>
    extends RestfulRouteProvider<ID> {

  default String fragmentName() {
    return "bs5";
  }

  TaggingService<LT, TT, ID> getTaggingService();

  @GetMapping(path = "/taggings")
  default ModelAndView globalTaggingIndex(ModelAndView mav) {
    mav.setViewName("sbu/taggings/global/index :: " + fragmentName());

    return mav;
  }

}
