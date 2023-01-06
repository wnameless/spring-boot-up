package com.github.wnameless.spring.boot.up.thymeleaf;

import java.util.HashSet;
import java.util.Set;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

public class AjaxDialect extends AbstractProcessorDialect {

  public AjaxDialect() {
    super("Ajax Dialect", "ajax", 1000);
  }

  public Set<IProcessor> getProcessors(final String dialectPrefix) {
    final Set<IProcessor> processors = new HashSet<IProcessor>();
    processors.add(new AjaxTargetTagProcessor(dialectPrefix));
    return processors;
  }

}
