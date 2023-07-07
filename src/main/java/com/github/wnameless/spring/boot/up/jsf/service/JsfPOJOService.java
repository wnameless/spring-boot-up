package com.github.wnameless.spring.boot.up.jsf.service;

import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import com.github.wnameless.spring.boot.up.jsf.JsfConfig;
import com.github.wnameless.spring.boot.up.jsf.JsonCoreFactory;
import com.github.wnameless.spring.boot.up.jsf.JsonSchemaFormUtils;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public final class JsfPOJOService {

  private final Map<String, Map<String, Object>> schemaCache = new HashMap<>();

  public Map<String, Object> getSchemaTemplate(String formType, Locale locale) {
    String templatePath = JsfConfig.getTemplatePath() + "/" + formType + "/" + formType + ".schema."
        + locale + ".json";
    if (schemaCache.containsKey(templatePath)) return schemaCache.get(templatePath);

    try {
      URL schemaUrl = Resources.getResource(templatePath);
      String json = Resources.toString(schemaUrl, Charsets.UTF_8);
      Map<String, Object> template = JsonCoreFactory.INSTANCE.readJson(json).asObject().toMap();
      schemaCache.put(templatePath, template);
      return template;
    } catch (Exception e) {
      return null;
    }
  }

  public Map<String, Object> getSchemaTemplate(String formType) {
    Locale locale = LocaleContextHolder.getLocale();
    Map<String, Object> template = getSchemaTemplate(formType, locale);
    if (template != null) return template;

    String templatePath =
        JsfConfig.getTemplatePath() + "/" + formType + "/" + formType + ".schema.json";
    if (schemaCache.containsKey(templatePath)) return schemaCache.get(templatePath);

    try {
      URL schemaUrl = Resources.getResource(templatePath);
      String json = Resources.toString(schemaUrl, Charsets.UTF_8);
      template = JsonCoreFactory.INSTANCE.readJson(json).asObject().toMap();
      schemaCache.put(templatePath, template);
      return template;
    } catch (Exception e) {
      log.info("Schema template not found", e);
      return JsonSchemaFormUtils.defaultSchema();
    }
  }

  public Map<String, Object> getUiSchemaTemplate(String formType, Locale locale) {
    String templatePath = JsfConfig.getTemplatePath() + "/" + formType + "/" + formType
        + ".uiSchema." + locale + ".json";
    if (schemaCache.containsKey(templatePath)) return schemaCache.get(templatePath);

    try {
      URL schemaUrl = Resources.getResource(templatePath);
      String json = Resources.toString(schemaUrl, Charsets.UTF_8);
      Map<String, Object> template = JsonCoreFactory.INSTANCE.readJson(json).asObject().toMap();
      schemaCache.put(templatePath, template);
      return template;
    } catch (Exception e) {
      return null;
    }
  }

  public Map<String, Object> getUiSchemaTemplate(String formType) {
    Locale locale = LocaleContextHolder.getLocale();
    Map<String, Object> template = getUiSchemaTemplate(formType, locale);
    if (template != null) return template;

    String templatePath =
        JsfConfig.getTemplatePath() + "/" + formType + "/" + formType + ".uiSchema.json";
    if (schemaCache.containsKey(templatePath)) return schemaCache.get(templatePath);

    try {
      URL schemaUrl = Resources.getResource(templatePath);
      String json = Resources.toString(schemaUrl, Charsets.UTF_8);
      template = JsonCoreFactory.INSTANCE.readJson(json).asObject().toMap();
      schemaCache.put(templatePath, template);
      return template;
    } catch (Exception e) {
      log.info("UiSchema template not found", e);
      return JsonSchemaFormUtils.defaultUiSchema();
    }
  }

}
