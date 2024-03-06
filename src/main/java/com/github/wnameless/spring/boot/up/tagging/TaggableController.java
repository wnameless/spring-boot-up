package com.github.wnameless.spring.boot.up.tagging;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.RestfulJsonSchemaForm;
import com.github.wnameless.spring.boot.up.permission.PermittedUser;
import com.github.wnameless.spring.boot.up.web.ModelAttributes.Item;
import com.github.wnameless.spring.boot.up.web.RestfulItemProvider;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import lombok.SneakyThrows;

public interface TaggableController<E extends Taggable<E, T, UL, L, ID>, TS extends TaggingService<T, UL, L, ID>, T extends TagTemplate<UL, L, ID>, UL extends UserLabelTemplate<ID>, L extends LabelTemplate<ID>, ID>
    extends RestfulRouteProvider<ID>, RestfulItemProvider<E> {

  default String getFragmentName() {
    return "bs5";
  }

  default E getTaggable(ID id) {
    return getRestfulItem();
  }

  @SuppressWarnings("unchecked")
  default TS getTaggingService() {
    return (TS) SpringBootUp.getBean(TaggingService.class);
  }

  @SneakyThrows
  default RestfulJsonSchemaForm<?> createEditForm(E taggable, ID id) {
    var editform = new RestfulJsonSchemaForm<String>(getRestfulRoute().getShowPath(id), "taggings");
    var mapper = SpringBootUp.getBean(ObjectMapper.class);

    String schema = """
        {
          "title": "註記標籤",
          "type": "object",
          "required": [],
          "properties": {
            "labelList": {
              "type": "array",
              "title": "公用標籤",
              "items": {
                "type": "string"
              },
              "uniqueItems": true
            },
            "userLabelList": {
              "type": "array",
              "title": "私人標籤",
              "items": {
                "type": "string"
              },
              "uniqueItems": true
            }
          }
        }
            """;
    var schemaMap = mapper.readValue(schema, new TypeReference<Map<String, Object>>() {});
    DocumentContext docCtx = JsonPath.parse(schemaMap);
    docCtx.put("$.properties.labelList.items", "enum",
        taggable.getLabelTemplates().stream().map(l -> l.getId()).toList());
    docCtx.put("$.properties.labelList.items", "enumNames",
        taggable.getLabelTemplates().stream().map(l -> l.getLabelName()).toList());
    docCtx.put("$.properties.userLabelList.items", "enum",
        taggable.getUserLabelTemplates().stream().map(l -> l.getId()).toList());
    docCtx.put("$.properties.userLabelList.items", "enumNames",
        taggable.getUserLabelTemplates().stream().map(l -> l.getLabelName()).toList());
    schemaMap = docCtx.read("$", new TypeRef<Map<String, Object>>() {});
    editform.setSchema(schemaMap);

    String uiSchema = """
        {
          "labelList": {
            "ui:widget": "checkboxes"
          },
          "userLabelList": {
            "ui:widget": "checkboxes"
          }
        }
             """;
    editform.setUiSchema(mapper.readValue(uiSchema, new TypeReference<Map<String, Object>>() {}));

    var tags = taggable.getTagTemplates();
    String formDataSchema = """
        {
          "labelList": [],
          "userLabelList": []
        }
            """;
    docCtx = JsonPath.parse(formDataSchema);
    docCtx.put("$", "labelList", tags.stream().filter(t -> t.getLabelTemplate() != null)
        .map(l -> l.getLabelTemplate().getId()).toList());
    docCtx.put("$", "userLabelList", tags.stream().filter(t -> t.getUserLabelTemplate() != null)
        .map(l -> l.getUserLabelTemplate().getId()).toList());
    editform.setFormData(docCtx.read("$", new TypeRef<Map<String, Object>>() {}));

    return editform;
  }

  @GetMapping("/{id}/taggings/edit")
  default ModelAndView editTaggings(ModelAndView mav, @PathVariable ID id) {
    mav.setViewName("sbu/taggings/edit :: " + getFragmentName());

    mav.addObject(Item.name(), createEditForm(getRestfulItem(), id));
    return mav;
  }

  @PutMapping("/{id}/taggings")
  default ModelAndView updateTaggings(ModelAndView mav, @PathVariable ID id,
      @RequestBody TaggableSchemaData<ID> data) {
    mav.setViewName("sbu/taggings/show :: " + getFragmentName());

    getTaggingService().getTagTemplateRepository().findAllByEntityId(id)
        .forEach(tag -> getTaggingService().getTagTemplateRepository().delete(tag));

    for (var labelId : data.getLabelList()) {
      var tag = getTaggingService().newTagTemplate();
      tag.setEntityId(id);
      var label = getTaggingService().getLabelTemplateRepository().findById(labelId);
      if (label.isEmpty()) continue;
      tag.setLabelTemplate(label.get());
      tag.setUsername(SpringBootUp.getBean(PermittedUser.class).getUsername());
      getTaggingService().getTagTemplateRepository().save(tag);
    }
    for (var userLabelId : data.getUserLabelList()) {
      var tag = getTaggingService().newTagTemplate();
      tag.setEntityId(id);
      var userLabel = getTaggingService().getUserLabelTemplateRepository().findById(userLabelId);
      if (userLabel.isEmpty()) continue;
      tag.setUserLabelTemplate(userLabel.get());
      tag.setUsername(SpringBootUp.getBean(PermittedUser.class).getUsername());
      getTaggingService().getTagTemplateRepository().save(tag);
    }

    mav.addObject(Item.name(), createEditForm(getRestfulItem(), id));
    return mav;
  }

}
