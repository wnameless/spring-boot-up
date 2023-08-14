package com.github.wnameless.spring.boot.up.attachment;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.spring.boot.up.SpringBootUp;
import com.github.wnameless.spring.boot.up.jsf.RestfulJsonSchemaForm;
import com.github.wnameless.spring.boot.up.web.RestfulItemProvider;
import com.github.wnameless.spring.boot.up.web.RestfulRouteProvider;
import com.github.wnameless.spring.boot.up.web.WebModelAttribute;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

public interface AttachmentSnapshotController<AA extends AttachmentSnapshotAware<AA, A, ID>, S extends AttachmentService<A, ID>, A extends Attachment<ID>, ID>
    extends RestfulItemProvider<AA>, RestfulRouteProvider<ID> {

  default AA getAttachmentSnapshotAware(ID id) {
    return getRestfulItem();
  }

  @SuppressWarnings("unchecked")
  default S getAttachmentService() {
    return (S) SpringBootUp.getBean(AttachmentService.class);
  }

  default void updateSnapshot(AA attachmentSnapshotAware, List<A> attachments) {
    var original = attachmentSnapshotAware.getSnapshot().getAttachments();
    if (original == null) original = new ArrayList<>();

    for (var a : attachments) {
      if (attachmentSnapshotAware.isValidAttachment(a)) {
        if (attachmentSnapshotAware.isExistedAttachment(a)) {
          attachmentSnapshotAware.removeExistedAttachment(a);
        }
        original.add(a);
      }
    }

    attachmentSnapshotAware.getSnapshot().setAttachments(original);
    attachmentSnapshotAware.saveAttachmentSnapshotAware();
  }

  @SneakyThrows
  default RestfulJsonSchemaForm<?> createEditForm(AttachmentChecklist checklist,
      AttachmentSnapshot<A, ID> snapshot, ID id) {
    var editform =
        new RestfulJsonSchemaForm<String>(getRestfulRoute().getShowPath(id), "attachments");
    var mapper = SpringBootUp.getBean(ObjectMapper.class);
    var attachmentsGroups = snapshot.getAttachmentsByGroup();

    editform.getSchema().put("title", "Files");
    editform.getSchema().put("type", "object");
    var aryProps = new LinkedHashMap<>();
    String ary = """
        {
          "type": "array",
          "items": {
            "type": "string"
          }
        }
          """;
    for (String group : checklist.getGroupNames().stream()
        .filter(gn -> attachmentsGroups.keySet().contains(gn)).toList()) {
      aryProps.put(group, mapper.readValue(ary, Map.class));
    }
    editform.getSchema().put("properties", aryProps);

    String uiOpt = """
        {
          "ui:options": {
            "addable": false,
            "orderable": false,
            "removable": true
          }
        }
           """;
    for (String group : attachmentsGroups.keySet()) {
      editform.getUiSchema().put(group, mapper.readValue(uiOpt, Map.class));
    }

    for (String group : attachmentsGroups.keySet()) {
      editform.getFormData().put(group,
          attachmentsGroups.get(group).stream().map(a -> a.getName()).toList());
    }

    return editform;
  }

  @SneakyThrows
  default RestfulJsonSchemaForm<?> createUploadForm(AttachmentChecklist checklist, ID id) {
    var uploadform =
        new RestfulJsonSchemaForm<String>(getRestfulRoute().getShowPath(id) + "/attachments", "");
    var mapper = SpringBootUp.getBean(ObjectMapper.class);

    uploadform.getSchema().put("title", "Files");
    uploadform.getSchema().put("type", "object");
    var fileProps = new LinkedHashMap<>();
    String single = """
        {
          "type": "string",
          "format": "data-url"
        }
          """;
    String mutiple = """
        {
          "type": "array",
          "items": {
            "type": "string",
            "format": "data-url"
          }
        }
          """;
    for (var ag : checklist.getAttachmentGroups()) {
      if (ag.isSingle()) {
        fileProps.put(ag.getGroup(), mapper.readValue(single, Map.class));
      } else {
        fileProps.put(ag.getGroup(), mapper.readValue(mutiple, Map.class));
      }
    }
    uploadform.getSchema().put("properties", fileProps);

    return uploadform;
  }

  @GetMapping("/{id}/attachments")
  default ModelAndView retrieveAttachments(ModelAndView mav, @PathVariable ID id,
      @RequestParam(required = true) String ajaxTargetId) {
    mav.setViewName("sbu/attachments/snapshot :: panel");

    var attachmentSnapshotAware = getAttachmentSnapshotAware(id);
    var snapshot = attachmentSnapshotAware.getSnapshot();
    var checklist = attachmentSnapshotAware.getChecklist();

    mav.addObject("attachmentChecklist", checklist);
    mav.addObject("attachmentGroups", snapshot.getAttachmentsByGroup());
    mav.addObject("ajaxTargetId", ajaxTargetId);
    return mav;
  }

  @GetMapping("/{id}/attachments/edit")
  default ModelAndView editAttachments(ModelAndView mav, @PathVariable ID id,
      @RequestParam(required = true) String ajaxTargetId) {
    mav.setViewName("sbu/attachments/snapshot :: edit");

    var attachmentSnapshotAware = getAttachmentSnapshotAware(id);
    var checklist = attachmentSnapshotAware.getChecklist();
    var snapshot = attachmentSnapshotAware.getSnapshot();

    mav.addObject(WebModelAttribute.ITEM, createEditForm(checklist, snapshot, id));
    mav.addObject("ajaxTargetId", ajaxTargetId);
    return mav;
  }

  @GetMapping("/{id}/attachments/upload")
  default ModelAndView uploadFragment(ModelAndView mav, @PathVariable ID id,
      @RequestParam(required = true) String ajaxTargetId) {
    mav.setViewName("sbu/attachments/snapshot :: upload");

    var attachmentSnapshotAware = getAttachmentSnapshotAware(id);
    var checklist = attachmentSnapshotAware.getChecklist();

    mav.addObject(WebModelAttribute.ITEM, createUploadForm(checklist, id));
    mav.addObject("ajaxTargetId", ajaxTargetId);
    return mav;
  }

  @PostMapping("/{id}/attachments")
  default ModelAndView uploadAttachments(ModelAndView mav, @PathVariable ID id,
      @RequestBody Map<String, Object> jsfFiles,
      @RequestParam(required = true) String ajaxTargetId) {
    mav.setViewName("sbu/attachments/snapshot :: panel");

    var attachmentSnapshotAware = getAttachmentSnapshotAware(id);
    var service = getAttachmentService();

    List<A> attachments = new ArrayList<>();
    jsfFiles.entrySet().forEach(pair -> {
      String group = pair.getKey();
      if (pair.getValue() instanceof Collection) {
        Collection<?> fs = (Collection<?>) pair.getValue();
        fs.stream().forEach(f -> {
          if (f instanceof String) {
            var data = new Base64EncodingAttachmentableData((String) f);

            A a = getAttachmentService().newAttachment();
            a.setGroup(group);
            a.setName(data.getName());
            a.setCreatedAt(LocalDateTime.now());
            a.setUri(service.writeData(data.getBytes()));
            service.saveAttachment(a);

            attachments.add(a);
          }

        });
      } else if (pair.getValue() instanceof String) {
        var data = new Base64EncodingAttachmentableData((String) pair.getValue());

        A a = getAttachmentService().newAttachment();
        a.setGroup(group);
        a.setName(data.getName());
        a.setCreatedAt(LocalDateTime.now());
        a.setUri(service.writeData(data.getBytes()));
        service.saveAttachment(a);

        attachments.add(a);
      }
    });
    updateSnapshot(attachmentSnapshotAware, attachments);

    mav.addObject("attachmentChecklist", attachmentSnapshotAware.getChecklist());
    mav.addObject("attachmentGroups",
        attachmentSnapshotAware.getSnapshot().getAttachmentsByGroup());
    mav.addObject("ajaxTargetId", ajaxTargetId);
    return mav;
  }


  @GetMapping(path = "/{id}/attachments/{attachmentId}")
  default void downloadAttachment(HttpServletResponse response, @PathVariable ID id,
      @PathVariable ID attachmentId) {
    var attachmentSnapshot = getAttachmentSnapshotAware(id).getSnapshot();
    Optional<A> attachmentOpt = attachmentSnapshot.findAttachment(attachmentId);

    if (attachmentOpt.isEmpty()) return;

    A attachment = attachmentOpt.get();
    try (InputStream inputStream = getAttachmentService().readData(attachment)) {
      response.setContentType("application/octet-stream");
      response.setHeader("Content-Disposition", "attachment; filename=" + attachment.getName());

      int nRead;
      while ((nRead = inputStream.read()) != -1) {
        response.getWriter().write(nRead);
      }
    } catch (IOException e) {}
  }

  @PutMapping(path = "/{id}/attachments")
  default ModelAndView deleteAttachments(ModelAndView mav, @PathVariable ID id,
      @RequestBody Map<String, Object> jsfFiles,
      @RequestParam(required = true) String ajaxTargetId) {
    mav.setViewName("sbu/attachments/snapshot :: panel");

    var attachmentSnapshotAware = getAttachmentSnapshotAware(id);
    var original = attachmentSnapshotAware.getSnapshot().getAttachments();
    var filtered = new ArrayList<A>();

    jsfFiles.entrySet().forEach(pair -> {
      String group = pair.getKey();
      if (pair.getValue() instanceof Collection) {
        Collection<?> names = (Collection<?>) pair.getValue();
        names.stream().forEach(name -> {
          if (name instanceof String) {
            filtered.addAll(original.stream()
                .filter(
                    a -> Objects.equals(a.getGroup(), group) && Objects.equals(a.getName(), name))
                .toList());
          }
        });
      } else if (pair.getValue() instanceof String) {
        var name = pair.getValue();
        filtered.addAll(original.stream()
            .filter(a -> Objects.equals(a.getGroup(), group) && Objects.equals(a.getName(), name))
            .toList());
      }
    });
    attachmentSnapshotAware.getSnapshot().setAttachments(filtered);
    attachmentSnapshotAware.saveAttachmentSnapshotAware();

    mav.addObject("attachmentChecklist", attachmentSnapshotAware.getChecklist());
    mav.addObject("attachmentGroups",
        attachmentSnapshotAware.getSnapshot().getAttachmentsByGroup());
    mav.addObject("ajaxTargetId", ajaxTargetId);
    return mav;
  }

}
