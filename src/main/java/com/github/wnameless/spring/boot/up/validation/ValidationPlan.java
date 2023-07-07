package com.github.wnameless.spring.boot.up.validation;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

public interface ValidationPlan<T> {

  Class<T> getTargetType();

  ConstraintMapping getConstraintMapping();

  default Validator getValidator() {
    HibernateValidatorConfiguration configuration =
        Validation.byProvider(HibernateValidator.class).configure();
    return configuration.addMapping(getConstraintMapping()).buildValidatorFactory().getValidator();
  }

}
