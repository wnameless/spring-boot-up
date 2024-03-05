package com.github.wnameless.spring.boot.up.validation.validator.support;

import java.util.Collection;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.expression.TypeConverter;

public class RelaxedBooleanTypeConverterDecorator implements TypeConverter {

  private static final TypeDescriptor BOOLEAN_TYPE = TypeDescriptor.valueOf(Boolean.class);
  private static final TypeDescriptor NUMBER_TYPE = TypeDescriptor.valueOf(Number.class);

  private final TypeConverter decorated;

  /**
   * @param decorated converter that will handle all unsupported conversions
   */
  public RelaxedBooleanTypeConverterDecorator(TypeConverter decorated) {
    this.decorated = decorated;
  }

  public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
    return targetType.isAssignableTo(BOOLEAN_TYPE) && (sourceType.isAssignableTo(NUMBER_TYPE)
        || sourceType.isCollection() || sourceType.isArray())
        || decorated.canConvert(sourceType, targetType);
  }

  public Object convertValue(Object value, TypeDescriptor sourceType, TypeDescriptor targetType) {
    if (targetType.isAssignableTo(BOOLEAN_TYPE)) {
      if (value instanceof Number) {
        return ((Number) value).intValue() != 0;
      }
      if (sourceType.isCollection()) {
        return !((Collection<?>) value).isEmpty();
      }
      if (sourceType.isArray()) {
        return ((Object[]) value).length != 0;
      }
      return value;

    } else {
      return decorated.convertValue(value, sourceType, targetType);
    }
  }

}
