package com.github.wnameless.spring.boot.up.jsf.service;

import java.util.Map;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaForm;
import lombok.Data;
import lombok.experimental.Accessors;

@RequestScope
@Service
@Accessors(fluent = true)
@Data
public class JsfPatchService {

  private Function<? super JsonSchemaForm, Map<String, Object>> schemaPatch;
  private Function<? super JsonSchemaForm, Map<String, Object>> uiSchemaPatch;
  private Function<? super JsonSchemaForm, Map<String, Object>> formDataPatch;
  private Function<? super JsonSchemaForm, ? extends JsonSchemaForm> wholePatch;

}
