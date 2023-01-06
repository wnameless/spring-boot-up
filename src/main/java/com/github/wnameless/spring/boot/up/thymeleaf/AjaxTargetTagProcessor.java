package com.github.wnameless.spring.boot.up.thymeleaf;

import org.thymeleaf.standard.processor.AbstractStandardAttributeModifierTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class AjaxTargetTagProcessor extends AbstractStandardAttributeModifierTagProcessor {

  public static final int PRECEDENCE = 1000;
  public static final String ATTR_NAME = "target";
  public static final String TARGET_ATTR_COMPLETE_NAME = "ajax-target";

  public AjaxTargetTagProcessor(final String dialectPrefix) {
    super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, TARGET_ATTR_COMPLETE_NAME, PRECEDENCE, false,
        true);
  }

}
