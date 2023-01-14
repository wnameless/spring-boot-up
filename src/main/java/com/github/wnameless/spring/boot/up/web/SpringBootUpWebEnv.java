package com.github.wnameless.spring.boot.up.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.Data;

@Component("springBootUpWebEnv")
@Data
public class SpringBootUpWebEnv {

  @Value("${spring.boot.up.web.env.ajax-target-id:ajaxTarget}")
  private String ajaxTargetId;
  @Value("${spring.boot.up.web.env.model-attr-messages:messages}")
  private String modelAttrMessages;

}
