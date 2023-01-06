package com.github.wnameless.spring.boot.up.thymeleaf;

import org.thymeleaf.standard.processor.AbstractStandardAttributeModifierTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class HtmxPostTagProcessor extends AbstractStandardAttributeModifierTagProcessor {

  public static final int PRECEDENCE = 1000;
  public static final String ATTR_NAME = "post";
  public static final String TARGET_ATTR_COMPLETE_NAME = "hx-post";

  public HtmxPostTagProcessor(final String dialectPrefix) {
    super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, TARGET_ATTR_COMPLETE_NAME, PRECEDENCE, false,
        true);
  }

}
