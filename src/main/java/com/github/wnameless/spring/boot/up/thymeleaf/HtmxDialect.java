package com.github.wnameless.spring.boot.up.thymeleaf;

import java.util.HashSet;
import java.util.Set;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;

public class HtmxDialect extends AbstractProcessorDialect {

  public HtmxDialect() {
    super("Htmx Dialect", "hx", 1000);
  }

  public Set<IProcessor> getProcessors(final String dialectPrefix) {
    final Set<IProcessor> processors = new HashSet<IProcessor>();
    processors.add(new HtmxGetTagProcessor(dialectPrefix));
    processors.add(new HtmxPostTagProcessor(dialectPrefix));
    processors.add(new HtmxDeleteTagProcessor(dialectPrefix));
    processors.add(new HtmxHxTargetTagProcessor(dialectPrefix));
    processors.add(new HtmxConfirmTagProcessor(dialectPrefix));
    return processors;
  }

}
