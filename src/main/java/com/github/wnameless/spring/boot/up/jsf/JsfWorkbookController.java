package com.github.wnameless.spring.boot.up.jsf;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.github.wnameless.spring.boot.up.web.RestfulItemProvider;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;

public interface JsfWorkbookController<E extends JsfPOJO<SD>, SD, ID>
    extends RestfulRouteProvider<ID>, RestfulItemProvider<E> {

  byte[] getWorkbookTemplateFile(E jsfPojo, MultiValueMap<String, String> params);

  default String getWorkbookTemplateFileName(MultiValueMap<String, String> params) {
    return "workbook-template.xlsx";
  }

  @GetMapping("/jsf-workbook-template")
  default ResponseEntity<byte[]> downloadJsfWorkbookTemplate(ModelAndView mav,
      @RequestParam MultiValueMap<String, String> params) {
    return ResponseEntity.ok()
        .contentType(MediaType
            .parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + getWorkbookTemplateFileName(params) + "\"")
        .body(getWorkbookTemplateFile(getRestfulItem(), params));
  }

  void readWorkbookTemplateFile(MultipartFile workbookFile, MultiValueMap<String, String> params,
      RedirectAttributes redirectAttr);

  @PostMapping("/jsf-workbook-template")
  default ModelAndView uploadJsfWorkbookTemplate(ModelAndView mav,
      @RequestParam("workbook") MultipartFile workbookFile,
      @RequestParam MultiValueMap<String, String> params, RedirectAttributes redirectAttr) {
    readWorkbookTemplateFile(workbookFile, params, redirectAttr);
    mav.setViewName("redirect:" + getRestfulRoute().getIndexPath());
    return mav;
  }

}
