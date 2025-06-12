package com.github.wnameless.spring.boot.up.fsm;

import java.util.function.BiFunction;
import java.util.function.BooleanSupplier;
import org.springframework.core.GenericTypeResolver;
import com.github.wnameless.spring.boot.up.jsf.JsfVersioning;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaForm;
import com.github.wnameless.spring.boot.up.jsf.RestfulVersioningJsonSchemaForm;

public interface StateFormAdvice<SF extends JsonSchemaForm & JsfVersioning, PA extends PhaseProvider<?, ?, ?, ?>> {

  @SuppressWarnings("unchecked")
  default Class<SF> getStateFormType() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), StateFormAdvice.class);
    return (Class<SF>) genericTypeResolver[0];
  }

  @SuppressWarnings("unchecked")
  default Class<PA> getPhaseAwareType() {
    var genericTypeResolver =
        GenericTypeResolver.resolveTypeArguments(this.getClass(), StateFormAdvice.class);
    return (Class<PA>) genericTypeResolver[1];
  }

  default int getOrder() {
    return 0;
  }

  default BooleanSupplier activeStatus() {
    return () -> true;
  }

  BiFunction<PA, RestfulVersioningJsonSchemaForm<?>, RestfulVersioningJsonSchemaForm<?>> afterLoad();

  default BiFunction<PA, RestfulVersioningJsonSchemaForm<?>, RestfulVersioningJsonSchemaForm<?>> beforeSave() {
    return null;
  }

  default BiFunction<PA, RestfulVersioningJsonSchemaForm<?>, RestfulVersioningJsonSchemaForm<?>> afterSave() {
    return null;
  }

}
