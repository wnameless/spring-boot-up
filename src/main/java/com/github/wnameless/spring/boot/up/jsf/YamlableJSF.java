package com.github.wnameless.spring.boot.up.jsf;

import com.github.wnameless.json.yamlifier.YamlifierUtils;

public interface YamlableJSF extends JsonSchemaForm {

  default String toYaml() {
    return YamlifierUtils.toYamlWithComments(getSchema(), getFormData());
  }

}
