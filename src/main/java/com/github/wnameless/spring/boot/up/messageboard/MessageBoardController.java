package com.github.wnameless.spring.boot.up.messageboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;
import com.github.wnameless.spring.boot.up.web.TemplateFragmentAware;

public interface MessageBoardController<ID>
    extends RestfulRouteProvider<ID>, TemplateFragmentAware {

  @GetMapping("/board-ids/{boardId}/notices")
  default ModelAndView getBoardNotices(ModelAndView mav, @PathVariable("boardId") String boardId) {
    mav.setViewName("sbu/message-boards/notices :: " + getFragmentName());
    mav.addObject("notices",
        SpringBootUp.getBean(MessageBoardService.class).getNoticesByBoardId(boardId));
    return mav;
  }

}
