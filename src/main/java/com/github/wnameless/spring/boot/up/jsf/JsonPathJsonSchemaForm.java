package com.github.wnameless.spring.boot.up.jsf;

import static lombok.AccessLevel.PRIVATE;
import java.util.LinkedHashMap;
import java.util.Map;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = PRIVATE)
public class JsonPathJsonSchemaForm implements JsonSchemaForm {

  DocumentContext schemaContext = JsonPath.parse(new LinkedHashMap<String, Object>());
  DocumentContext uiSchemaContext = JsonPath.parse(new LinkedHashMap<String, Object>());
  DocumentContext formDataContext = JsonPath.parse(new LinkedHashMap<String, Object>());

  @Override
  public Map<String, Object> getSchema() {
    return schemaContext.json();
  }

  @Override
  public void setSchema(Map<String, Object> schema) {
    schemaContext = JsonPath.parse(schema);
  }

  @Override
  public Map<String, Object> getUiSchema() {
    return uiSchemaContext.json();
  }

  @Override
  public void setUiSchema(Map<String, Object> uiSchema) {
    uiSchemaContext = JsonPath.parse(uiSchema);
  }

  @Override
  public Map<String, Object> getFormData() {
    return formDataContext.json();
  }

  @Override
  public void setFormData(Map<String, Object> formData) {
    formDataContext = JsonPath.parse(formData);
  }

}
