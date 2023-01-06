/*
 *
 * Copyright 2019 Wei-Ming Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 */
package com.github.wnameless.spring.boot.up.jsf;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleJsonSchemaForm implements JsonSchemaForm {

  private Map<String, Object> formData = new LinkedHashMap<>();

  private Map<String, Object> schema = new LinkedHashMap<>();

  private Map<String, Object> uiSchema = new LinkedHashMap<>();

  @Override
  public Map<String, Object> getFormData() {
    return formData;
  }

  @Override
  public void setFormData(Map<String, Object> formData) {
    this.formData = formData;
  }

  @Override
  public Map<String, Object> getSchema() {
    return schema;
  }

  @Override
  public void setSchema(Map<String, Object> schema) {
    this.schema = schema;
  }

  @Override
  public Map<String, Object> getUiSchema() {
    return uiSchema;
  }

  @Override
  public void setUiSchema(Map<String, Object> uiSchema) {
    this.uiSchema = uiSchema;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((formData == null) ? 0 : formData.hashCode());
    result = prime * result + ((schema == null) ? 0 : schema.hashCode());
    result = prime * result + ((uiSchema == null) ? 0 : uiSchema.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SimpleJsonSchemaForm other = (SimpleJsonSchemaForm) obj;
    if (formData == null) {
      if (other.formData != null) return false;
    } else if (!formData.equals(other.formData)) return false;
    if (schema == null) {
      if (other.schema != null) return false;
    } else if (!schema.equals(other.schema)) return false;
    if (uiSchema == null) {
      if (other.uiSchema != null) return false;
    } else if (!uiSchema.equals(other.uiSchema)) return false;
    return true;
  }

  @Override
  public String toString() {
    return "SimpleReactJsonSchemaForm [formData=" + formData + ", schema=" + schema + ", uiSchema="
        + uiSchema + "]";
  }

}
