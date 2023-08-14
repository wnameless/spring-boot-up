package com.github.wnameless.spring.boot.up.fsm;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import com.github.wnameless.spring.boot.up.jsf.JsfVersioning;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaForm;

public interface StateFormAdvice<SF extends JsonSchemaForm & JsfVersioning, PA extends PhaseAware<?, ?, ?, ?>> {

  Class<SF> getStateFormType();

  Class<PA> getPhaseAwareType();

  default int getOrder() {
    return 0;
  }

  default BooleanSupplier activeStatus() {
    return () -> true;
  }

  BiFunction<PA, SF, SF> afterLoad();

  BiFunction<PA, SF, SF> beforeSave();

  BiFunction<PA, SF, SF> afterSave();

}
