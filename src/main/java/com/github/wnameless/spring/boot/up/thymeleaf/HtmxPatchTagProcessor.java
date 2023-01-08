package com.github.wnameless.spring.boot.up.thymeleaf;

import org.thymeleaf.standard.processor.AbstractStandardAttributeModifierTagProcessor;
import org.thymeleaf.templatemode.TemplateMode;

public class HtmxPatchTagProcessor extends AbstractStandardAttributeModifierTagProcessor {

  public static final int PRECEDENCE = 1000;
  public static final String ATTR_NAME = "patch";
  public static final String TARGET_ATTR_COMPLETE_NAME = "hx-patch";

  public HtmxPatchTagProcessor(final String dialectPrefix) {
    super(TemplateMode.HTML, dialectPrefix, ATTR_NAME, TARGET_ATTR_COMPLETE_NAME, PRECEDENCE, false,
        true);
  }

}
