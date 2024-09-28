package com.github.wnameless.spring.boot.up.validation;

import java.util.LinkedHashSet;
import java.util.Set;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaForm;
import com.networknt.schema.InputFormat;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion.VersionFlag;
import com.networknt.schema.ValidationMessage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidJSFValidator implements ConstraintValidator<ValidJSF, JsonSchemaForm> {

  @Override
  public boolean isValid(JsonSchemaForm value, ConstraintValidatorContext context) {
    if (value == null) {
      return true; // null values are considered valid
    }

    boolean isValid = true;
    Set<ValidationMessage> validationMessages = new LinkedHashSet<>();
    try {
      var mapper = new ObjectMapper();

      var schemaJson = mapper.writeValueAsString(value.getSchema());
      var formDataJson = mapper.writeValueAsString(value.getFormData());

      JsonSchemaFactory jsonSchemaFactory = JsonSchemaFactory.getInstance(VersionFlag.V202012);
      var schema = jsonSchemaFactory.getSchema(schemaJson);

      validationMessages.addAll(schema.validate(formDataJson, InputFormat.JSON));
      if (validationMessages.size() > 0) isValid = false;
    } catch (JsonProcessingException e) {}

    if (!isValid) {
      // Override the default message template
      context.disableDefaultConstraintViolation();

      for (var validationMessage : validationMessages) {
        context.buildConstraintViolationWithTemplate(validationMessage.getMessage())
            .addPropertyNode(validationMessage.getProperty()).addConstraintViolation();
      }
    }

    return isValid;
  }

}
