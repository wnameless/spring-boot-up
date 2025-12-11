package com.github.wnameless.spring.boot.up.attachment;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.rubycollect4j.Ruby;

public interface SubcategoryAttachmentSnapshotController<AA extends AttachmentSnapshotProvider<A, ID>, S extends AttachmentService<A, ID>, A extends Attachment<ID>, ID>
    extends AttachmentSnapshotControllerBase<S, A, ID> {

  AA getAttachmentSnapshotProvider(ID id, ID subId);

  @GetMapping("/{id}/subcategories/{subId}/attachments")
  default ModelAndView retrieveSubAttachments(ModelAndView mav, @PathVariable ID id,
      @PathVariable ID subId, @RequestParam String ajaxTargetId) {
    return retrieveAttachmentsAction(mav, getAttachmentSnapshotProvider(id, subId), ajaxTargetId);
  }

  @GetMapping("/{id}/subcategories/{subId}/attachments/edit")
  default ModelAndView editSubAttachments(ModelAndView mav, @PathVariable ID id,
      @PathVariable ID subId, @RequestParam String ajaxTargetId) {
    return editAttachmentsAction(mav, getAttachmentSnapshotProvider(id, subId),
        Ruby.Array.of(id, "subcategories", subId).join("/"), ajaxTargetId);
  }

  @GetMapping("/{id}/subcategories/{subId}/attachments/note")
  default ModelAndView noteSubAttachments(ModelAndView mav, @PathVariable ID id,
      @PathVariable ID subId, @RequestParam String ajaxTargetId) {
    return noteAttachmentsAction(mav, getAttachmentSnapshotProvider(id, subId),
        Ruby.Array.of(id, "subcategories", subId).join("/"), ajaxTargetId);
  }

  @GetMapping("/{id}/subcategories/{subId}/attachments/upload")
  default ModelAndView uploadSubFragment(ModelAndView mav, @PathVariable ID id,
      @PathVariable ID subId, @RequestParam String ajaxTargetId) {
    return uploadFragmentAction(mav, getAttachmentSnapshotProvider(id, subId),
        Ruby.Array.of(id, "subcategories", subId).join("/"), ajaxTargetId);
  }

  @PostMapping("/{id}/subcategories/{subId}/attachments")
  default ModelAndView uploadSubAttachments(ModelAndView mav, @PathVariable ID id,
      @PathVariable ID subId, @RequestBody Map<String, Object> jsfFiles,
      @RequestParam String ajaxTargetId) {
    return uploadAttachmentsAction(mav, getAttachmentSnapshotProvider(id, subId), jsfFiles,
        ajaxTargetId);
  }

  @GetMapping(path = "/{id}/subcategories/{subId}/attachments/{attachmentId}")
  default void downloadSubAttachment(HttpServletResponse response, @PathVariable ID id,
      @PathVariable ID subId, @PathVariable ID attachmentId) {
    downloadAttachmentAction(response, getAttachmentSnapshotProvider(id, subId), attachmentId);
  }

  @GetMapping(path = "/{id}/subcategories/{subId}/attachments/download-all")
  default void downloadAllSubAttachments(HttpServletResponse response, @PathVariable ID id,
      @PathVariable ID subId) {
    downloadAllAttachmentsAction(response, getAttachmentSnapshotProvider(id, subId));
  }

  @PutMapping(path = "/{id}/subcategories/{subId}/attachments")
  default ModelAndView modifySubAttachments(ModelAndView mav, @PathVariable ID id,
      @PathVariable ID subId, @RequestBody Map<String, Object> jsfFiles,
      @RequestParam String ajaxTargetId) {
    return modifyAttachmentsAction(mav, getAttachmentSnapshotProvider(id, subId), jsfFiles,
        ajaxTargetId);
  }

}
